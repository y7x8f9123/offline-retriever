package com.offlineretriever.vector;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RetrieverTest {

    @Test
    public void testSearchReturnsMostSimilarResult() {
        VectorStore store = new VectorStore();

        store.add(new VectorRecord(
                "1",
                "sample1.txt",
                new float[]{1.0f, 2.0f, 3.0f}
        ));

        store.add(new VectorRecord(
                "2",
                "sample2.txt",
                new float[]{3.0f, 2.0f, 1.0f}
        ));

        Retriever retriever = new Retriever(store);

        List<SearchResult> results = retriever.search(
                new float[]{1.0f, 2.0f, 3.0f},
                1
        );

        assertEquals(1, results.size());
        assertEquals(
                "sample1.txt",
                results.get(0).getRecord().getFileName()
        );
    }

    @Test
    public void testEmptyStoreReturnsEmptyList() {
        VectorStore store = new VectorStore();
        Retriever retriever = new Retriever(store);

        List<SearchResult> results = retriever.search(
                new float[]{1.0f, 2.0f, 3.0f},
                5
        );

        assertTrue(results.isEmpty());
    }
}