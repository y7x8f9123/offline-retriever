package com.offlineretriever.vector;

public class SimilarityCalculator {

    /**
     * Calculate cosine similarity between two vectors.
     */
    public static double cosineSimilarity(float[] vectorA, float[] vectorB) {

        if (vectorA == null || vectorB == null) {
            throw new IllegalArgumentException("Vectors cannot be null.");
        }

        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension.");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}