package com.offlineretriever.embedding;

public class TextEmbeddingEngine implements EmbeddingEngine<String> {

    @Override
    public float[] embed(String input) {

        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be empty.");
        }

        // Temporary placeholder vector.
        // This will later be replaced by BERT output.
        return new float[]{1.0f, 2.0f, 3.0f};
    }
}