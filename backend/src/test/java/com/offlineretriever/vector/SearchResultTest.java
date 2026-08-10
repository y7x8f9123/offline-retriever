package com.offlineretriever.vector;

import org.junit.Test;

import static org.junit.Assert.*;

public class SearchResultTest {

    @Test
    public void testConstructorAndGetters() {
        VectorRecord record =
                new VectorRecord(
                        "1",
                        "sample.txt",
                        new float[]{1.0f, 2.0f}
                );

        SearchResult result =
                new SearchResult(record, 0.95);

        assertSame(record, result.getRecord());
        assertEquals(0.95, result.getSimilarityScore(), 0.0001);
    }

    @Test
    public void testToString() {
        VectorRecord record =
                new VectorRecord(
                        "1",
                        "sample.txt",
                        new float[]{1.0f, 2.0f}
                );

        SearchResult result =
                new SearchResult(record, 0.85);

        String output = result.toString();

        assertTrue(output.contains("SearchResult"));
        assertTrue(output.contains("sample.txt"));
        assertTrue(output.contains("0.85"));
    }
}