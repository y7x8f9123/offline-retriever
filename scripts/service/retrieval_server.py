from contextlib import asynccontextmanager
from pathlib import Path
import hashlib
import sys

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import uvicorn


PROJECT_ROOT = Path(__file__).resolve().parents[2]

EMBEDDING_DIR = PROJECT_ROOT / "scripts" / "embedding"
STORAGE_DIR = PROJECT_ROOT / "scripts" / "storage"

for path in (EMBEDDING_DIR, STORAGE_DIR):
    value = str(path)

    if value not in sys.path:
        sys.path.insert(0, value)


from chroma_store import ChromaStore
from text_embedding import TextEmbedding
from image_embedding import ImageEmbedding


CHUNK_SIZE = 400
CHUNK_OVERLAP = 50
CHUNK_SEARCH_MULTIPLIER = 5

# MobileCLIP cosine similarity scores are generally lower
# than BERT scores in the current retrieval pipeline.
# This calibration factor was selected through local testing.
IMAGE_SCORE_CALIBRATION = 1.25


class Runtime:
    def __init__(self):
        self.store = None
        self.text_engine = None
        self.image_engine = None


runtime = Runtime()


class TextIndexRequest(BaseModel):
    file_path: str
    content: str


class ImageIndexRequest(BaseModel):
    file_path: str


class SearchRequest(BaseModel):
    query: str
    top_k: int = 5


class DeleteRequest(BaseModel):
    id: str


def make_file_id(
    file_path: str,
) -> str:
    normalized = str(
        Path(file_path).resolve()
    ).lower()

    return hashlib.sha256(
        normalized.encode("utf-8")
    ).hexdigest()


def make_chunk_id(
    file_id: str,
    chunk_index: int,
) -> str:
    return (
        f"{file_id}"
        f"_chunk_"
        f"{chunk_index}"
    )


def file_extension(
    path: Path,
) -> str:
    return (
        path.suffix
        .lower()
        .lstrip(".")
    )


def split_text(
    content: str,
    chunk_size: int = CHUNK_SIZE,
    overlap: int = CHUNK_OVERLAP,
) -> list[str]:
    words = content.split()

    if not words:
        return []

    if overlap >= chunk_size:
        raise ValueError(
            "Chunk overlap must be smaller "
            "than chunk size."
        )

    chunks = []
    start = 0

    while start < len(words):
        end = min(
            start + chunk_size,
            len(words),
        )

        chunk = " ".join(
            words[start:end]
        ).strip()

        if chunk:
            chunks.append(chunk)

        if end >= len(words):
            break

        start = end - overlap

    return chunks


def build_metadata(
    path: Path,
    content_type: str,
) -> dict:
    stat = path.stat()

    return {
        "fileName": path.name,
        "filePath": str(path.resolve()),
        "fileType": file_extension(path),
        "fileSize": stat.st_size,
        "lastModified": stat.st_mtime * 1000,
        "contentType": content_type,
    }


def result_list(
    result: dict,
) -> list[dict]:
    ids = result.get(
        "ids",
        [[]],
    )[0]

    metadatas = result.get(
        "metadatas",
        [[]],
    )[0]

    distances = result.get(
        "distances",
        [[]],
    )[0]

    output = []

    for record_id, metadata, distance in zip(
        ids,
        metadatas,
        distances,
    ):
        file_id = metadata.get(
            "fileId",
            record_id,
        )

        raw_score = (
            1.0
            - float(distance)
        )

        output.append(
            {
                "id": file_id,
                "recordId": record_id,
                "fileName": metadata.get(
                    "fileName",
                    "",
                ),
                "filePath": metadata.get(
                    "filePath",
                    "",
                ),
                "fileType": metadata.get(
                    "fileType",
                    "",
                ),
                "contentType": metadata.get(
                    "contentType",
                    "",
                ),
                "chunkIndex": metadata.get(
                    "chunkIndex",
                    -1,
                ),
                "rawScore": raw_score,
                "score": raw_score,
            }
        )

    return output


def aggregate_file_results(
    results: list[dict],
) -> list[dict]:
    best_by_file = {}

    for item in results:
        file_id = item["id"]

        existing = best_by_file.get(
            file_id
        )

        if (
            existing is None
            or item["rawScore"]
            > existing["rawScore"]
        ):
            best_by_file[file_id] = item

    output = list(
        best_by_file.values()
    )

    output.sort(
        key=lambda item:
            item["rawScore"],
        reverse=True,
    )

    return output


def calibrate_results(
    results: list[dict],
    calibration_factor: float,
) -> list[dict]:
    calibrated = []

    for item in results:
        updated = {
            **item,
            "score":
                item["rawScore"]
                * calibration_factor,
        }

        calibrated.append(
            updated
        )

    return calibrated


@asynccontextmanager
async def lifespan(
    app: FastAPI,
):
    print(
        "Starting Offline Retriever backend..."
    )

    print(
        "Opening ChromaDB..."
    )

    runtime.store = ChromaStore()

    print(
        "Loading BERT..."
    )

    runtime.text_engine = TextEmbedding()

    print(
        "Loading MobileCLIP..."
    )

    runtime.image_engine = ImageEmbedding()

    print(
        "Offline Retriever backend ready."
    )

    yield

    print(
        "Offline Retriever backend stopped."
    )


app = FastAPI(
    title="Offline Retriever Local Backend",
    lifespan=lifespan,
)


@app.get("/health")
def health():
    return {
        "status": "ok",
        "text_records":
            runtime.store.text_count(),
        "image_records":
            runtime.store.image_count(),
        "bert_loaded":
            runtime.text_engine is not None,
        "mobileclip_loaded":
            runtime.image_engine is not None,
    }


@app.get("/files")
def list_files():
    files = (
        runtime.store
        .get_all_files()
    )

    for item in files:
        path = item.get(
            "filePath",
            "",
        )

        item["exists"] = (
            Path(path).is_file()
        )

    return files


@app.post("/index-text")
def index_text(
    request: TextIndexRequest,
):
    path = Path(
        request.file_path
    )

    if not path.is_file():
        raise HTTPException(
            status_code=404,
            detail="File not found.",
        )

    content = request.content

    if (
        content is None
        or not content.strip()
    ):
        raise HTTPException(
            status_code=400,
            detail="Text content is empty.",
        )

    extension = file_extension(
        path
    )

    if extension not in {
        "txt",
        "pdf",
        "docx",
    }:
        raise HTTPException(
            status_code=400,
            detail="Unsupported text file type.",
        )

    chunks = split_text(
        content
    )

    if not chunks:
        raise HTTPException(
            status_code=400,
            detail="No text chunks were generated.",
        )

    file_id = make_file_id(
        str(path)
    )

    base_metadata = build_metadata(
        path,
        "text",
    )

    chunk_count = len(
        chunks
    )

    embedding_dimension = 0

    for chunk_index, chunk in enumerate(
        chunks
    ):
        embedding = (
            runtime.text_engine
            .embed(chunk)
        )

        if embedding_dimension == 0:
            embedding_dimension = len(
                embedding
            )

        record_id = make_chunk_id(
            file_id,
            chunk_index,
        )

        metadata = {
            **base_metadata,
            "fileId": file_id,
            "chunkIndex": chunk_index,
            "chunkCount": chunk_count,
        }

        runtime.store.add_text_file(
            record_id=record_id,
            embedding=embedding.tolist(),
            metadata=metadata,
            document=chunk,
        )

    return {
        "status": "ok",
        "id": file_id,
        "contentType": "text",
        "chunkCount": chunk_count,
        "dimension": embedding_dimension,
    }


@app.post("/index-image")
def index_image(
    request: ImageIndexRequest,
):
    path = Path(
        request.file_path
    )

    if not path.is_file():
        raise HTTPException(
            status_code=404,
            detail="File not found.",
        )

    extension = file_extension(
        path
    )

    if extension not in {
        "jpg",
        "jpeg",
        "png",
    }:
        raise HTTPException(
            status_code=400,
            detail="Unsupported image file type.",
        )

    embedding = (
        runtime.image_engine
        .embed_image(
            str(path)
        )
    )

    file_id = make_file_id(
        str(path)
    )

    metadata = build_metadata(
        path,
        "image",
    )

    runtime.store.add_image_file(
        file_id=file_id,
        embedding=embedding.tolist(),
        metadata=metadata,
    )

    return {
        "status": "ok",
        "id": file_id,
        "contentType": "image",
        "dimension": len(
            embedding
        ),
    }


@app.post("/search")
def search(
    request: SearchRequest,
):
    query = request.query.strip()

    if not query:
        raise HTTPException(
            status_code=400,
            detail="Query cannot be empty.",
        )

    top_k = max(
        1,
        request.top_k,
    )

    text_embedding = (
        runtime.text_engine
        .embed(query)
    )

    chunk_search_k = (
        top_k
        * CHUNK_SEARCH_MULTIPLIER
    )

    text_result = (
        runtime.store
        .search_text(
            text_embedding.tolist(),
            chunk_search_k,
        )
    )

    text_results = result_list(
        text_result
    )

    text_results = (
        aggregate_file_results(
            text_results
        )
    )

    image_results = []

    if (
        runtime.store
        .image_count()
        > 0
    ):
        image_embedding = (
            runtime.image_engine
            .embed_text(query)
        )

        image_result = (
            runtime.store
            .search_images(
                image_embedding.tolist(),
                top_k,
            )
        )

        image_results = result_list(
            image_result
        )

        image_results = calibrate_results(
            image_results,
            IMAGE_SCORE_CALIBRATION,
        )

    combined = (
        text_results
        + image_results
    )

    combined.sort(
        key=lambda item:
            item["score"],
        reverse=True,
    )

    return combined[:top_k]


@app.post("/delete")
def delete_file(
    request: DeleteRequest,
):
    runtime.store.delete_file(
        request.id
    )

    return {
        "status": "ok",
        "id": request.id,
    }


if __name__ == "__main__":
    uvicorn.run(
        app,
        host="127.0.0.1",
        port=8765,
        log_level="info",
    )