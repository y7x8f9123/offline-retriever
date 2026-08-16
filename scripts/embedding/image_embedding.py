from pathlib import Path

import open_clip
import torch
from PIL import Image


MODEL_NAME = "MobileCLIP-S1"
PRETRAINED = "datacompdr"

PROJECT_ROOT = Path(__file__).resolve().parents[2]


class ImageEmbedding:

    def __init__(self):
        self.device = torch.device("cpu")

        self.model, _, self.preprocess = (
            open_clip.create_model_and_transforms(
                MODEL_NAME,
                pretrained=PRETRAINED,
            )
        )

        self.tokenizer = open_clip.get_tokenizer(
            MODEL_NAME
        )

        self.model.to(self.device)
        self.model.eval()

    def embed_image(
        self,
        image_path: str,
    ):
        image = (
            Image.open(image_path)
            .convert("RGB")
        )

        image_tensor = (
            self.preprocess(image)
            .unsqueeze(0)
            .to(self.device)
        )

        with torch.no_grad():
            embedding = self.model.encode_image(
                image_tensor,
                normalize=True,
            )

        return (
            embedding
            .cpu()
            .numpy()[0]
        )

    def embed_text(
        self,
        text: str,
    ):
        if text is None or not text.strip():
            raise ValueError(
                "Input text cannot be empty."
            )

        tokens = self.tokenizer(
            [text]
        ).to(self.device)

        with torch.no_grad():
            embedding = self.model.encode_text(
                tokens,
                normalize=True,
            )

        return (
            embedding
            .cpu()
            .numpy()[0]
        )

    # Backward-compatible alias.
    def embed(
        self,
        image_path: str,
    ):
        return self.embed_image(
            image_path
        )


if __name__ == "__main__":
    engine = ImageEmbedding()

    test_image = PROJECT_ROOT / "mobileclip_test.png"

    image_vector = engine.embed_image(
        str(test_image)
    )

    text_vector = engine.embed_text(
        "a red image"
    )

    print(
        "Image embedding length:",
        len(image_vector),
    )

    print(
        "Text embedding length:",
        len(text_vector),
    )

    print(
        "Image first 10:",
        image_vector[:10],
    )

    print(
        "Text first 10:",
        text_vector[:10],
    )