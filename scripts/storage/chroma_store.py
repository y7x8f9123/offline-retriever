from pathlib import Path
from typing import Any

import chromadb


PROJECT_ROOT = Path(__file__).resolve().parents[2]
DB_PATH = PROJECT_ROOT / "chroma_db"
COLLECTION_NAME = "offline_retriever_files"


class ChromaStore:
    def __init__(self):
        self.client = chromadb.PersistentClient(
            path=str(DB_PATH)
        )

        self.collection = self.client.get_or_create_collection(
            name=COLLECTION_NAME,
            metadata={
                "description": "Offline Retriever local file embeddings"
            },
            configuration={
                "hnsw": {
                    "space": "cosine"
                }
            }
        )

    def add_file(
        self,
        file_id: str,
        embedding: list[float],
        metadata: dict[str, Any],
        document: str = "",
    ) -> None:
        self.collection.upsert(
            ids=[file_id],
            embeddings=[embedding],
            metadatas=[metadata],
            documents=[document],
        )

    def search(
        self,
        query_embedding: list[float],
        top_k: int = 5,
    ) -> dict:
        return self.collection.query(
            query_embeddings=[query_embedding],
            n_results=top_k,
            include=[
                "metadatas",
                "documents",
                "distances",
            ],
        )

    def delete_file(self, file_id: str) -> None:
        self.collection.delete(
            ids=[file_id]
        )

    def get_all_files(self) -> dict:
        return self.collection.get(
            include=[
                "metadatas",
                "documents",
            ]
        )

    def count(self) -> int:
        return self.collection.count()


if __name__ == "__main__":
    store = ChromaStore()

    store.delete_file("test-file-001")

    print("ChromaDB path:", DB_PATH)
    print("Collection:", COLLECTION_NAME)
    print("Stored records:", store.count())