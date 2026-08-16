from pathlib import Path

import torch
from transformers import AutoModel, AutoTokenizer


MODEL_NAME = "bert-base-uncased"

PROJECT_ROOT = Path(__file__).resolve().parents[2]
LOCAL_MODEL_PATH = PROJECT_ROOT / "models" / "bert-base-uncased"


class TextEmbedding:

    def __init__(self):
        self.device = torch.device("cpu")

        # If the model has already been downloaded into the project,
        # always load it locally so runtime does not require internet.
        if LOCAL_MODEL_PATH.exists():
            model_source = str(LOCAL_MODEL_PATH)

            self.tokenizer = AutoTokenizer.from_pretrained(
                model_source,
                local_files_only=True,
            )

            self.model = AutoModel.from_pretrained(
                model_source,
                local_files_only=True,
            )

        else:
            print("Downloading BERT model for first-time setup...")

            self.tokenizer = AutoTokenizer.from_pretrained(
                MODEL_NAME
            )

            self.model = AutoModel.from_pretrained(
                MODEL_NAME
            )

            LOCAL_MODEL_PATH.mkdir(
                parents=True,
                exist_ok=True,
            )

            self.tokenizer.save_pretrained(
                LOCAL_MODEL_PATH
            )

            self.model.save_pretrained(
                LOCAL_MODEL_PATH
            )

            print(
                "BERT model saved locally at:",
                LOCAL_MODEL_PATH,
            )

        self.model.to(self.device)
        self.model.eval()

    def embed(self, text: str):
        if text is None or not text.strip():
            raise ValueError(
                "Input text cannot be empty."
            )

        inputs = self.tokenizer(
            text,
            return_tensors="pt",
            truncation=True,
            padding=True,
            max_length=512,
        )

        inputs = {
            key: value.to(self.device)
            for key, value in inputs.items()
        }

        with torch.no_grad():
            outputs = self.model(**inputs)

        # Mean pooling over non-padding tokens.
        token_embeddings = outputs.last_hidden_state
        attention_mask = inputs["attention_mask"]

        expanded_mask = (
            attention_mask
            .unsqueeze(-1)
            .expand(token_embeddings.size())
            .float()
        )

        summed = torch.sum(
            token_embeddings * expanded_mask,
            dim=1,
        )

        counts = torch.clamp(
            expanded_mask.sum(dim=1),
            min=1e-9,
        )

        embedding = summed / counts

        # Normalize for cosine similarity.
        embedding = torch.nn.functional.normalize(
            embedding,
            p=2,
            dim=1,
        )

        return embedding.cpu().numpy()[0]


if __name__ == "__main__":
    engine = TextEmbedding()

    vector = engine.embed(
        "software engineering"
    )

    print("Embedding length:", len(vector))
    print("First 10 values:", vector[:10])