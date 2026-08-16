from pathlib import Path
from typing import Any

import chromadb


PROJECT_ROOT = Path(__file__).resolve().parents[2]
DB_PATH = PROJECT_ROOT / "chroma_db"

TEXT_COLLECTION = "offline_retriever_text"
IMAGE_COLLECTION = "offline_retriever_images"


class ChromaStore:
    def __init__(self):
        self.client = chromadb.PersistentClient(
            path=str(DB_PATH)
        )

        self.text_collection = self.client.get_or_create_collection(
            name=TEXT_COLLECTION,
            metadata={
                "description": "BERT text embeddings"
            },
            configuration={
                "hnsw": {
                    "space": "cosine"
                }
            }
        )

        self.image_collection = self.client.get_or_create_collection(
            name=IMAGE_COLLECTION,
            metadata={
                "description": "MobileCLIP image embeddings"
            },
            configuration={
                "hnsw": {
                    "space": "cosine"
                }
            }
        )

    def add_text_file(
        self,
        file_id: str,
        embedding: list[float],
        metadata: dict[str, Any],
        document: str = "",
    ) -> None:
        self.text_collection.upsert(
            ids=[file_id],
            embeddings=[embedding],
            metadatas=[metadata],
            documents=[document],
        )

    def add_image_file(
        self,
        file_id: str,
        embedding: list[float],
        metadata: dict[str, Any],
    ) -> None:
        self.image_collection.upsert(
            ids=[file_id],
            embeddings=[embedding],
            metadatas=[metadata],
        )

    def search_text(
        self,
        query_embedding: list[float],
        top_k: int = 5,
    ) -> dict:
        count = self.text_collection.count()

        if count == 0:
            return {
                "ids": [[]],
                "metadatas": [[]],
                "distances": [[]],
            }

        return self.text_collection.query(
            query_embeddings=[query_embedding],
            n_results=min(top_k, count),
            include=[
                "metadatas",
                "distances",
            ],
        )

    def search_images(
        self,
        query_embedding: list[float],
        top_k: int = 5,
    ) -> dict:
        count = self.image_collection.count()

        if count == 0:
            return {
                "ids": [[]],
                "metadatas": [[]],
                "distances": [[]],
            }

        return self.image_collection.query(
            query_embeddings=[query_embedding],
            n_results=min(top_k, count),
            include=[
                "metadatas",
                "distances",
            ],
        )

    def delete_file(self, file_id: str) -> None:
        self.text_collection.delete(
            ids=[file_id]
        )

        self.image_collection.delete(
            ids=[file_id]
        )

    def get_all_files(self) -> list[dict]:
        output = []

        text_result = self.text_collection.get(
            include=["metadatas"]
        )

        for file_id, metadata in zip(
            text_result.get("ids", []),
            text_result.get("metadatas", []),
        ):
            output.append(
                {
                    "id": file_id,
                    **metadata,
                }
            )

        image_result = self.image_collection.get(
            include=["metadatas"]
        )

        for file_id, metadata in zip(
            image_result.get("ids", []),
            image_result.get("metadatas", []),
        ):
            output.append(
                {
                    "id": file_id,
                    **metadata,
                }
            )

        return output

    def text_count(self) -> int:
        return self.text_collection.count()

    def image_count(self) -> int:
        return self.image_collection.count()


if __name__ == "__main__":
    store = ChromaStore()

    print("ChromaDB path:", DB_PATH)
    print("Text records:", store.text_count())
    print("Image records:", store.image_count())