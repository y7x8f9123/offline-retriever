from pathlib import Path

import open_clip
import torch
from PIL import Image


MODEL_NAME = "MobileCLIP-S1"
PRETRAINED = "datacompdr"

PROJECT_ROOT = Path(__file__).resolve().parents[2]
IMAGE_PATH = PROJECT_ROOT / "backend" / "sample.png"


class ImageEmbedding:

    def __init__(self):
        print("Loading MobileCLIP model...")

        self.model, _, self.preprocess = open_clip.create_model_and_transforms(
            MODEL_NAME,
            pretrained=PRETRAINED
        )

        self.model.eval()

        print("MobileCLIP loaded.")

    def embed(self, image_path):
        image = Image.open(image_path).convert("RGB")
        image_tensor = self.preprocess(image).unsqueeze(0)

        with torch.no_grad():
            embedding = self.model.encode_image(image_tensor)

        embedding = embedding / embedding.norm(dim=-1, keepdim=True)

        return embedding.cpu().numpy()[0]


if __name__ == "__main__":
    engine = ImageEmbedding()

    vector = engine.embed(IMAGE_PATH)

    print("Image path:", IMAGE_PATH)
    print("Embedding length:", len(vector))
    print("First 10 values:", vector[:10])