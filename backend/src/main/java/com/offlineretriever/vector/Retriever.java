package com.offlineretriever.vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Retriever {

    private final VectorStore vectorStore;

    public Retriever(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Search the vector store using cosine similarity.
     */
    public List<SearchResult> search(float[] queryVector, int topK) {

        List<SearchResult> results = new ArrayList<>();

        for (VectorRecord record : vectorStore.getAllRecords()) {

            double similarity = SimilarityCalculator.cosineSimilarity(
                    queryVector,
                    record.getEmbedding());

            results.add(new SearchResult(record, similarity));
        }

        results.sort(
                Comparator.comparingDouble(SearchResult::getSimilarityScore)
                        .reversed());

        if (topK < results.size()) {
            return results.subList(0, topK);
        }

        return results;
    }
}