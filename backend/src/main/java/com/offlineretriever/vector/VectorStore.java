package com.offlineretriever.vector;

import java.util.ArrayList;
import java.util.List;

public class VectorStore {

    private final List<VectorRecord> records;

    public VectorStore() {
        records = new ArrayList<>();
    }

    public void add(VectorRecord record) {
        records.add(record);
    }

    public List<VectorRecord> getAllRecords() {
        return records;
    }

    public int size() {
        return records.size();
    }

    public void clear() {
        records.clear();
    }
}