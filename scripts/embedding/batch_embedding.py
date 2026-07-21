from pathlib import Path

import numpy as np

from embedding_engine import EmbeddingEngine


DATASET_DIR = Path("dataset")
OUTPUT_DIR = Path("models/embeddings")

TEXT_EXTENSIONS = {".txt"}
IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg"}


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    print("Loading embedding engine...")
    engine = EmbeddingEngine()

    print("Scanning dataset...")

    processed_count = 0
    skipped_count = 0
    failed_count = 0

    for file in DATASET_DIR.rglob("*"):

        if not file.is_file():
            continue

        suffix = file.suffix.lower()

        try:
            if suffix in TEXT_EXTENSIONS:
                text = file.read_text(
                    encoding="utf-8",
                    errors="ignore"
                )

                if not text.strip():
                    print(f"[SKIP ] Empty text file: {file}")
                    skipped_count += 1
                    continue

                vector = engine.embed_text(text)

                relative_path = file.relative_to(DATASET_DIR)
                output_name = (
                    str(relative_path)
                    .replace("\\", "__")
                    .replace("/", "__")
                    + ".npy"
                )

                output_file = OUTPUT_DIR / output_name
                np.save(output_file, vector)

                print(
                    f"[TEXT ] {file} "
                    f"-> {output_file.name} "
                    f"({len(vector)} dimensions)"
                )

                processed_count += 1

            elif suffix in IMAGE_EXTENSIONS:
                vector = engine.embed_image(file)

                relative_path = file.relative_to(DATASET_DIR)
                output_name = (
                    str(relative_path)
                    .replace("\\", "__")
                    .replace("/", "__")
                    + ".npy"
                )

                output_file = OUTPUT_DIR / output_name
                np.save(output_file, vector)

                print(
                    f"[IMAGE] {file} "
                    f"-> {output_file.name} "
                    f"({len(vector)} dimensions)"
                )

                processed_count += 1

            else:
                print(f"[SKIP ] Unsupported file: {file}")
                skipped_count += 1

        except Exception as error:
            print(f"[ERROR] {file}: {error}")
            failed_count += 1

    print()
    print("Batch embedding completed.")
    print("Processed:", processed_count)
    print("Skipped:", skipped_count)
    print("Failed:", failed_count)
    print("Output directory:", OUTPUT_DIR.resolve())


if __name__ == "__main__":
    main()