import numpy as np
from pathlib import Path


EMBEDDING_DIR = Path("models/embeddings")


def cosine_similarity(vector1, vector2):
    return np.dot(vector1, vector2) / (
        np.linalg.norm(vector1) * np.linalg.norm(vector2)
    )


def main():
    vector1 = np.load(EMBEDDING_DIR / "text1.txt.npy")
    vector2 = np.load(EMBEDDING_DIR / "text2.txt.npy")

    similarity = cosine_similarity(vector1, vector2)

    print("Text 1 length:", len(vector1))
    print("Text 2 length:", len(vector2))
    print("Cosine similarity:", similarity)


if __name__ == "__main__":
    main()