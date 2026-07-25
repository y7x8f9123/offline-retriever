package com.offlineretriever.vector;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VectorStoreTest {

    @Test
    public void testAddRecord() {

        VectorStore store = new VectorStore();

        store.add(new VectorRecord(
                "1",
                "sample.txt",
                new float[]{1.0f, 2.0f, 3.0f}
        ));

        assertEquals(1, store.size());
    }

    @Test
    public void testClearStore() {

        VectorStore store = new VectorStore();

        store.add(new VectorRecord(
                "1",
                "sample.txt",
                new float[]{1.0f, 2.0f, 3.0f}
        ));

        store.clear();

        assertEquals(0, store.size());
    }
}