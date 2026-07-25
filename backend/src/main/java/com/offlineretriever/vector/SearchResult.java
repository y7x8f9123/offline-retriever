package com.offlineretriever.vector;

public class SearchResult {

    private final VectorRecord record;
    private final double similarityScore;

    public SearchResult(VectorRecord record, double similarityScore) {
        this.record = record;
        this.similarityScore = similarityScore;
    }

    public VectorRecord getRecord() {
        return record;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "record=" + record +
                ", similarityScore=" + similarityScore +
                '}';
    }
}