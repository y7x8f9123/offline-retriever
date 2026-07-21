from transformers import AutoTokenizer, TFAutoModel
import tensorflow as tf

MODEL_NAME = "bert-base-uncased"

print("Loading tokenizer...")
tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)

print("Loading model...")
model = TFAutoModel.from_pretrained(
    MODEL_NAME,
    from_pt=True
)

text = "Hello offline retriever."

inputs = tokenizer(
    text,
    return_tensors="tf",
    truncation=True,
    padding=True
)

outputs = model(**inputs)

embedding = outputs.last_hidden_state[:, 0, :]

print("Embedding shape:", embedding.shape)
print("First 10 values:", embedding.numpy()[0][:10])