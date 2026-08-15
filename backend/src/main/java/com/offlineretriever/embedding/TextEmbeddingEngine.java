package com.offlineretriever.embedding;

import java.util.Locale;

public class TextEmbeddingEngine implements EmbeddingEngine<String> {

    private static final int DIMENSION = 256;

    @Override
    public float[] embed(String input) {

        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Input text cannot be empty."
            );
        }

        float[] vector = new float[DIMENSION];

        String normalized = input
                .toLowerCase(Locale.ROOT)
                .trim();

        addEnglishTokens(normalized, vector);
        addChineseTokens(normalized, vector);

        return vector;
    }

    private void addEnglishTokens(
            String text,
            float[] vector
    ) {

        String cleaned = text.replaceAll(
                "[^a-z0-9\\s]",
                " "
        );

        String[] tokens = cleaned.trim().split("\\s+");

        for (String token : tokens) {
            if (!token.isEmpty()) {
                addToken(token, vector);
            }
        }
    }

    private void addChineseTokens(
            String text,
            float[] vector
    ) {

        String chinese = text.replaceAll(
                "[^\\p{IsHan}]",
                ""
        );

        if (chinese.isEmpty()) {
            return;
        }

        // Single Chinese characters
        for (int i = 0; i < chinese.length(); i++) {
            addToken(
                    chinese.substring(i, i + 1),
                    vector
            );
        }

        // Chinese two-character combinations
        for (int i = 0; i < chinese.length() - 1; i++) {
            addToken(
                    chinese.substring(i, i + 2),
                    vector
            );
        }
    }

    private void addToken(
            String token,
            float[] vector
    ) {

        int index = Math.floorMod(
                token.hashCode(),
                DIMENSION
        );

        vector[index] += 1.0f;
    }
}