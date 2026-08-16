import argparse
import hashlib
import json
import sys
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parents[2]
EMBEDDING_DIR = PROJECT_ROOT / "scripts" / "embedding"

if str(EMBEDDING_DIR) not in sys.path:
    sys.path.insert(0, str(EMBEDDING_DIR))

from chroma_store import ChromaStore


_text_embedding_engine = None
_image_embedding_engine = None


def get_text_embedding_engine():
    global _text_embedding_engine

    if _text_embedding_engine is None:
        from text_embedding import TextEmbedding

        _text_embedding_engine = TextEmbedding()

    return _text_embedding_engine


def get_image_embedding_engine():
    global _image_embedding_engine

    if _image_embedding_engine is None:
        from image_embedding import ImageEmbedding

        _image_embedding_engine = ImageEmbedding()

    return _image_embedding_engine


def make_file_id(file_path: str) -> str:
    normalized = str(
        Path(file_path).resolve()
    ).lower()

    return hashlib.sha256(
        normalized.encode("utf-8")
    ).hexdigest()


def read_stdin_text() -> str:
    text = sys.stdin.read()

    if text is None or not text.strip():
        raise ValueError(
            "No text was supplied through stdin."
        )

    return text


def command_index_text(args):
    text = read_stdin_text()

    engine = get_text_embedding_engine()
    embedding = engine.embed(text)

    store = ChromaStore()

    file_id = make_file_id(
        args.file_path
    )

    metadata = {
        "fileName": args.file_name,
        "filePath": args.file_path,
        "fileType": args.file_type,
        "fileSize": args.file_size,
        "lastModified": args.last_modified,
        "contentType": "text",
    }

    store.add_text_file(
        file_id=file_id,
        embedding=embedding.tolist(),
        metadata=metadata,
        document=text,
    )

    print(
        json.dumps(
            {
                "status": "ok",
                "id": file_id,
                "contentType": "text",
                "dimension": len(embedding),
            },
            ensure_ascii=False,
        )
    )


def command_index_image(args):
    engine = get_image_embedding_engine()

    embedding = engine.embed_image(
        args.file_path
    )

    store = ChromaStore()

    file_id = make_file_id(
        args.file_path
    )

    metadata = {
        "fileName": args.file_name,
        "filePath": args.file_path,
        "fileType": args.file_type,
        "fileSize": args.file_size,
        "lastModified": args.last_modified,
        "contentType": "image",
    }

    store.add_image_file(
        file_id=file_id,
        embedding=embedding.tolist(),
        metadata=metadata,
    )

    print(
        json.dumps(
            {
                "status": "ok",
                "id": file_id,
                "contentType": "image",
                "dimension": len(embedding),
            },
            ensure_ascii=False,
        )
    )


def convert_result(
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

    for file_id, metadata, distance in zip(
        ids,
        metadatas,
        distances,
    ):
        output.append(
            {
                "id": file_id,
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
                "score": 1.0 - float(distance),
            }
        )

    return output


def command_search_all(args):
    query = read_stdin_text()

    store = ChromaStore()

    text_engine = get_text_embedding_engine()

    text_embedding = text_engine.embed(
        query
    )

    text_result = store.search_text(
        query_embedding=text_embedding.tolist(),
        top_k=args.top_k,
    )

    text_output = convert_result(
        text_result
    )

    image_output = []

    if store.image_count() > 0:
        image_engine = get_image_embedding_engine()

        image_query_embedding = (
            image_engine.embed_text(
                query
            )
        )

        image_result = store.search_images(
            query_embedding=
                image_query_embedding.tolist(),
            top_k=args.top_k,
        )

        image_output = convert_result(
            image_result
        )

    combined = (
        text_output +
        image_output
    )

    combined.sort(
        key=lambda item: item["score"],
        reverse=True,
    )

    combined = combined[:args.top_k]

    print(
        json.dumps(
            combined,
            ensure_ascii=False,
        )
    )


def command_list(args):
    store = ChromaStore()

    print(
        json.dumps(
            store.get_all_files(),
            ensure_ascii=False,
        )
    )


def command_delete(args):
    store = ChromaStore()

    store.delete_file(
        args.id
    )

    print(
        json.dumps(
            {
                "status": "ok",
                "id": args.id,
            },
            ensure_ascii=False,
        )
    )


def add_file_arguments(
    parser,
):
    parser.add_argument(
        "--file-name",
        required=True,
    )

    parser.add_argument(
        "--file-path",
        required=True,
    )

    parser.add_argument(
        "--file-type",
        required=True,
    )

    parser.add_argument(
        "--file-size",
        type=int,
        required=True,
    )

    parser.add_argument(
        "--last-modified",
        type=float,
        required=True,
    )


def build_parser():
    parser = argparse.ArgumentParser(
        description=(
            "Offline Retriever "
            "multimodal ChromaDB bridge"
        )
    )

    subparsers = parser.add_subparsers(
        dest="command",
        required=True,
    )

    index_text_parser = (
        subparsers.add_parser(
            "index-text"
        )
    )

    add_file_arguments(
        index_text_parser
    )

    index_text_parser.set_defaults(
        function=command_index_text
    )

    index_image_parser = (
        subparsers.add_parser(
            "index-image"
        )
    )

    add_file_arguments(
        index_image_parser
    )

    index_image_parser.set_defaults(
        function=command_index_image
    )

    search_parser = (
        subparsers.add_parser(
            "search-all"
        )
    )

    search_parser.add_argument(
        "--top-k",
        type=int,
        default=5,
    )

    search_parser.set_defaults(
        function=command_search_all
    )

    list_parser = subparsers.add_parser(
        "list"
    )

    list_parser.set_defaults(
        function=command_list
    )

    delete_parser = subparsers.add_parser(
        "delete"
    )

    delete_parser.add_argument(
        "--id",
        required=True,
    )

    delete_parser.set_defaults(
        function=command_delete
    )

    return parser


def main():
    parser = build_parser()
    args = parser.parse_args()

    args.function(args)


if __name__ == "__main__":
    main()