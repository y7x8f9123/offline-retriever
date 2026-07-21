from transformers import AutoTokenizer, TFAutoModel

MODEL_NAME = "bert-base-uncased"


class TextEmbedding:

    def __init__(self):
        print("Loading BERT model...")
        self.tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)
        self.model = TFAutoModel.from_pretrained(
            MODEL_NAME,
            from_pt=True
        )
        print("BERT loaded.")

    def embed(self, text):
        inputs = self.tokenizer(
            text,
            return_tensors="tf",
            truncation=True,
            padding=True
        )

        outputs = self.model(**inputs)

        embedding = outputs.last_hidden_state[:, 0, :]

        return embedding.numpy()[0]


if __name__ == "__main__":
    engine = TextEmbedding()

    text = input("Enter text: ")

    vector = engine.embed(text)

    print("Embedding length:", len(vector))
    print("First 10 values:", vector[:10])