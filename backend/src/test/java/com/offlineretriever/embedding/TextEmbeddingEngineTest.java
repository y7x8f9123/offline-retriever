package com.offlineretriever.embedding;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

public class TextEmbeddingEngineTest {

    @Test
    public void shouldReturnEmbeddingForValidText() {

        TextEmbeddingEngine engine = new TextEmbeddingEngine();

        float[] result = engine.embed("Hello World");

        assertEquals(256, result.length);
    }

    @Test
    public void shouldRejectEmptyText() {

        TextEmbeddingEngine engine = new TextEmbeddingEngine();

        assertThrows(
                IllegalArgumentException.class,
                () -> engine.embed("")
        );
    }

    @Test
    public void shouldSupportChineseText() {

        TextEmbeddingEngine engine = new TextEmbeddingEngine();

        float[] first = engine.embed(
                "这是一个离线检索系统"
        );

        float[] second = engine.embed(
                "中文文档解析功能"
        );

        assertEquals(256, first.length);
        assertEquals(256, second.length);

        assertFalse(
                Arrays.equals(first, second)
        );
    }
}