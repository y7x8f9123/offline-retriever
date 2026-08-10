package com.offlineretriever.vector;

import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class VectorSearchBenchmarkTest {

    private static final int[] LIBRARY_SIZES = {1000, 10000, 50000};
    private static final int VECTOR_DIMENSION = 128;
    private static final int TOP_K = 10;

    private static final int WARM_UP_SEARCHES = 20;
    private static final int MEASURED_SEARCHES = 100;

    @Test
    public void testVectorSearchScalability() {

        System.out.println();
        System.out.println("===== VECTOR SEARCH SCALABILITY BENCHMARK =====");

        Random random = new Random(42);

        for (int librarySize : LIBRARY_SIZES) {

            VectorStore vectorStore = new VectorStore();

            // Build vector library
            for (int i = 0; i < librarySize; i++) {

                float[] embedding = new float[VECTOR_DIMENSION];

                for (int j = 0; j < VECTOR_DIMENSION; j++) {
                    embedding[j] = random.nextFloat();
                }

                vectorStore.add(
                        new VectorRecord(
                                "record-" + i,
                                "file-" + i + ".txt",
                                embedding
                        )
                );
            }

            Retriever retriever = new Retriever(vectorStore);

            float[] queryVector = new float[VECTOR_DIMENSION];

            for (int i = 0; i < VECTOR_DIMENSION; i++) {
                queryVector[i] = random.nextFloat();
            }

            // JVM/JIT warm-up
            for (int i = 0; i < WARM_UP_SEARCHES; i++) {
                retriever.search(queryVector, TOP_K);
            }

            double totalMs = 0.0;
            double minMs = Double.MAX_VALUE;
            double maxMs = 0.0;

            List<SearchResult> lastResults = null;

            // Measured searches
            for (int i = 0; i < MEASURED_SEARCHES; i++) {

                long start = System.nanoTime();

                lastResults =
                        retriever.search(queryVector, TOP_K);

                long end = System.nanoTime();

                double elapsedMs =
                        (end - start) / 1_000_000.0;

                totalMs += elapsedMs;

                minMs = Math.min(minMs, elapsedMs);
                maxMs = Math.max(maxMs, elapsedMs);
            }

            double averageMs =
                    totalMs / MEASURED_SEARCHES;

            System.out.println();
            System.out.println(
                    "Vector Library Size: " + librarySize
            );

            System.out.println(
                    "Vector Dimension: " + VECTOR_DIMENSION
            );

            System.out.println(
                    "Top K: " + TOP_K
            );

            System.out.printf(
                    "Average Search Latency: %.3f ms%n",
                    averageMs
            );

            System.out.printf(
                    "Minimum Search Latency: %.3f ms%n",
                    minMs
            );

            System.out.printf(
                    "Maximum Search Latency: %.3f ms%n",
                    maxMs
            );

            assertNotNull(lastResults);
            assertEquals(TOP_K, lastResults.size());
        }

        System.out.println();
        System.out.println(
                "===== VECTOR SEARCH BENCHMARK COMPLETED ====="
        );
    }
}