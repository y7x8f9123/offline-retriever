package com.offlineretriever.vector;

import org.junit.Test;

import static org.junit.Assert.*;

public class VectorRecordTest {

    @Test
    public void testConstructorAndGetters() {
        float[] embedding = {1.0f, 2.0f, 3.0f};

        VectorRecord record =
                new VectorRecord("1", "sample.txt", embedding);

        assertEquals("1", record.getId());
        assertEquals("sample.txt", record.getFileName());
        assertArrayEquals(embedding, record.getEmbedding(), 0.0001f);
    }

    @Test
    public void testSetters() {
        VectorRecord record =
                new VectorRecord("1", "old.txt", new float[]{1.0f});

        float[] newEmbedding = {4.0f, 5.0f};

        record.setId("2");
        record.setFileName("new.txt");
        record.setEmbedding(newEmbedding);

        assertEquals("2", record.getId());
        assertEquals("new.txt", record.getFileName());
        assertArrayEquals(newEmbedding, record.getEmbedding(), 0.0001f);
    }

    @Test
    public void testToStringWithEmbedding() {
        VectorRecord record =
                new VectorRecord(
                        "123",
                        "document.pdf",
                        new float[]{1.0f, 2.0f, 3.0f}
                );

        String result = record.toString();

        assertTrue(result.contains("123"));
        assertTrue(result.contains("document.pdf"));
        assertTrue(result.contains("embeddingLength=3"));
    }

    @Test
    public void testToStringWithNullEmbedding() {
        VectorRecord record =
                new VectorRecord("123", "document.pdf", null);

        assertTrue(record.toString().contains("embeddingLength=0"));
    }
}