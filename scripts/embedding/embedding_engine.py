from text_embedding import TextEmbedding
from image_embedding import ImageEmbedding


class EmbeddingEngine:

    def __init__(self):
        self.text_engine = TextEmbedding()
        self.image_engine = ImageEmbedding()

    def embed_text(self, text):
        return self.text_engine.embed(text)

    def embed_image(self, image_path):
        return self.image_engine.embed(image_path)


if __name__ == "__main__":
    engine = EmbeddingEngine()

    text_vector = engine.embed_text("Offline multimodal retrieval system")
    image_vector = engine.embed_image("backend/sample.png")

    print("Text embedding length:", len(text_vector))
    print("Image embedding length:", len(image_vector))