package com.offlineretriever.vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Retriever {

    private final VectorStore vectorStore;

    public Retriever(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Search the vector store using cosine similarity.
     *
     * Uses a fixed-size min-heap to avoid sorting
     * all records and reduce temporary object creation.
     */
    public List<SearchResult> search(float[] queryVector, int topK) {

        if (topK <= 0) {
            return new ArrayList<>();
        }

        PriorityQueue<SearchResult> topResults =
                new PriorityQueue<>(
                        Comparator.comparingDouble(
                                SearchResult::getSimilarityScore
                        )
                );

        for (VectorRecord record : vectorStore.getAllRecords()) {

            double similarity =
                    SimilarityCalculator.cosineSimilarity(
                            queryVector,
                            record.getEmbedding()
                    );

            if (topResults.size() < topK) {

                topResults.offer(
                        new SearchResult(record, similarity)
                );

            } else if (similarity >
                    topResults.peek().getSimilarityScore()) {

                topResults.poll();

                topResults.offer(
                        new SearchResult(record, similarity)
                );
            }
        }

        List<SearchResult> results =
                new ArrayList<>(topResults);

        results.sort(
                Comparator.comparingDouble(
                        SearchResult::getSimilarityScore
                ).reversed()
        );

        return results;
    }
}