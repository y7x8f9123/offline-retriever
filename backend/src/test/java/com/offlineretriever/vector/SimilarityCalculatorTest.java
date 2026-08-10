package com.offlineretriever.vector;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimilarityCalculatorTest {

    @Test
    public void testIdenticalVectorsHaveSimilarityOne() {
        float[] vectorA = {1.0f, 2.0f, 3.0f};
        float[] vectorB = {1.0f, 2.0f, 3.0f};

        double similarity =
                SimilarityCalculator.cosineSimilarity(vectorA, vectorB);

        assertEquals(1.0, similarity, 0.0001);
    }

    @Test
    public void testOrthogonalVectorsHaveSimilarityZero() {
        float[] vectorA = {1.0f, 0.0f};
        float[] vectorB = {0.0f, 1.0f};

        double similarity =
                SimilarityCalculator.cosineSimilarity(vectorA, vectorB);

        assertEquals(0.0, similarity, 0.0001);
    }

    @Test
    public void testOppositeVectorsHaveSimilarityNegativeOne() {
        float[] vectorA = {1.0f, 2.0f};
        float[] vectorB = {-1.0f, -2.0f};

        double similarity =
                SimilarityCalculator.cosineSimilarity(vectorA, vectorB);

        assertEquals(-1.0, similarity, 0.0001);
    }

    @Test
    public void testZeroFirstVectorReturnsZero() {
        float[] vectorA = {0.0f, 0.0f};
        float[] vectorB = {1.0f, 2.0f};

        double similarity =
                SimilarityCalculator.cosineSimilarity(vectorA, vectorB);

        assertEquals(0.0, similarity, 0.0001);
    }

    @Test
    public void testZeroSecondVectorReturnsZero() {
        float[] vectorA = {1.0f, 2.0f};
        float[] vectorB = {0.0f, 0.0f};

        double similarity =
                SimilarityCalculator.cosineSimilarity(vectorA, vectorB);

        assertEquals(0.0, similarity, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFirstVectorThrowsException() {
        SimilarityCalculator.cosineSimilarity(
                null,
                new float[]{1.0f, 2.0f}
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullSecondVectorThrowsException() {
        SimilarityCalculator.cosineSimilarity(
                new float[]{1.0f, 2.0f},
                null
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentDimensionsThrowException() {
        float[] vectorA = {1.0f, 2.0f};
        float[] vectorB = {1.0f, 2.0f, 3.0f};

        SimilarityCalculator.cosineSimilarity(vectorA, vectorB);
    }
}