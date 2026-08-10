package com.offlineretriever.vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VectorStore {

    private final List<VectorRecord> records;
    private final List<VectorRecord> readOnlyRecords;

    public VectorStore() {
        records = new ArrayList<>();
        readOnlyRecords = Collections.unmodifiableList(records);
    }

    public void add(VectorRecord record) {
        records.add(record);
    }

    public List<VectorRecord> getAllRecords() {
        return readOnlyRecords;
    }

    public int size() {
        return records.size();
    }

    public void clear() {
        records.clear();
    }
}