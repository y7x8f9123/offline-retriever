import argparse
import hashlib
import json
import sys
from pathlib import Path

# Make project scripts importable.
PROJECT_ROOT = Path(__file__).resolve().parents[2]
EMBEDDING_DIR = PROJECT_ROOT / "scripts" / "embedding"

if str(EMBEDDING_DIR) not in sys.path:
    sys.path.insert(0, str(EMBEDDING_DIR))

from chroma_store import ChromaStore


_embedding_engine = None


def get_embedding_engine():
    global _embedding_engine

    if _embedding_engine is None:
        from text_embedding import TextEmbedding
        _embedding_engine = TextEmbedding()

    return _embedding_engine


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

    engine = get_embedding_engine()
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

    store.add_file(
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
                "dimension": len(embedding),
            },
            ensure_ascii=False,
        )
    )


def command_search_text(args):
    query = read_stdin_text()

    engine = get_embedding_engine()
    embedding = engine.embed(query)

    store = ChromaStore()

    result = store.search(
        query_embedding=embedding.tolist(),
        top_k=args.top_k,
    )

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
                "score": 1.0 - float(distance),
            }
        )

    print(
        json.dumps(
            output,
            ensure_ascii=False,
        )
    )


def command_list(args):
    store = ChromaStore()

    result = store.get_all_files()

    ids = result.get(
        "ids",
        [],
    )

    metadatas = result.get(
        "metadatas",
        [],
    )

    output = []

    for file_id, metadata in zip(
        ids,
        metadatas,
    ):
        output.append(
            {
                "id": file_id,
                **metadata,
            }
        )

    print(
        json.dumps(
            output,
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


def build_parser():
    parser = argparse.ArgumentParser(
        description=(
            "Offline Retriever "
            "BERT + ChromaDB bridge"
        )
    )

    subparsers = parser.add_subparsers(
        dest="command",
        required=True,
    )

    index_parser = subparsers.add_parser(
        "index-text"
    )

    index_parser.add_argument(
        "--file-name",
        required=True,
    )

    index_parser.add_argument(
        "--file-path",
        required=True,
    )

    index_parser.add_argument(
        "--file-type",
        required=True,
    )

    index_parser.add_argument(
        "--file-size",
        type=int,
        required=True,
    )

    index_parser.add_argument(
        "--last-modified",
        type=float,
        required=True,
    )

    index_parser.set_defaults(
        function=command_index_text
    )

    search_parser = subparsers.add_parser(
        "search-text"
    )

    search_parser.add_argument(
        "--top-k",
        type=int,
        default=5,
    )

    search_parser.set_defaults(
        function=command_search_text
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