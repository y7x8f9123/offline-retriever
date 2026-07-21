package com.offlineretriever.embedding;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

public class TextEmbeddingEngineTest {

    @Test
    public void shouldReturnEmbeddingForValidText() {

        TextEmbeddingEngine engine = new TextEmbeddingEngine();

        float[] result = engine.embed("Hello World");

        assertArrayEquals(
                new float[]{1.0f, 2.0f, 3.0f},
                result,
                0.001f
        );
    }

    @Test
    public void shouldRejectEmptyText() {

        TextEmbeddingEngine engine = new TextEmbeddingEngine();

        assertThrows(
                IllegalArgumentException.class,
                () -> engine.embed("")
        );
    }
}