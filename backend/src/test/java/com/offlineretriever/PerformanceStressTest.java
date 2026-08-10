package com.offlineretriever;

import com.offlineretriever.vector.SearchResult;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PerformanceStressTest {

    private static final int[] TEST_SIZES = {100, 500, 1000};

    private static final int WARM_UP_SEARCHES = 20;
    private static final int MEASURED_SEARCHES = 100;

    @Test
    public void testLargeFileLibraryPerformance() throws Exception {

        System.out.println();
        System.out.println("===== PERFORMANCE BASELINE BENCHMARK =====");

        for (int fileCount : TEST_SIZES) {

            RetrievalPipeline pipeline = new RetrievalPipeline();
            List<File> files = new ArrayList<>();

            // Encourage GC before measuring approximate memory usage.
            System.gc();
            Thread.sleep(100);

            long memoryBefore = getUsedMemory();

            // Generate temporary test files.
            for (int i = 0; i < fileCount; i++) {

                File file = File.createTempFile(
                        "benchmark-" + i + "-",
                        ".txt"
                );

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(
                            "Document " + i +
                            " contains information about offline retrieval, " +
                            "local semantic search, vector embeddings, " +
                            "document processing and information retrieval."
                    );
                }

                files.add(file);
            }

            // Measure indexing performance.
            long indexStart = System.nanoTime();

            for (File file : files) {
                pipeline.indexFile(file);
            }

            long indexEnd = System.nanoTime();

            double totalIndexingMs =
                    (indexEnd - indexStart) / 1_000_000.0;

            double averageIndexingMs =
                    totalIndexingMs / fileCount;

            // Warm up JVM/JIT before search measurement.
            for (int i = 0; i < WARM_UP_SEARCHES; i++) {
                pipeline.search("offline semantic retrieval", 10);
            }

            double totalSearchMs = 0.0;
            double minSearchMs = Double.MAX_VALUE;
            double maxSearchMs = 0.0;

            List<SearchResult> lastResults = null;

            // Repeated search measurements.
            for (int i = 0; i < MEASURED_SEARCHES; i++) {

                long searchStart = System.nanoTime();

                lastResults =
                        pipeline.search(
                                "offline semantic retrieval",
                                10
                        );

                long searchEnd = System.nanoTime();

                double searchMs =
                        (searchEnd - searchStart) / 1_000_000.0;

                totalSearchMs += searchMs;

                minSearchMs =
                        Math.min(minSearchMs, searchMs);

                maxSearchMs =
                        Math.max(maxSearchMs, searchMs);
            }

            double averageSearchMs =
                    totalSearchMs / MEASURED_SEARCHES;

            System.gc();
            Thread.sleep(100);

            long memoryAfter = getUsedMemory();

            double memoryIncreaseMb =
                    (memoryAfter - memoryBefore)
                            / (1024.0 * 1024.0);

            System.out.println();
            System.out.println("Library Size: " + fileCount);

            System.out.printf(
                    "Total Indexing Time: %.3f ms%n",
                    totalIndexingMs
            );

            System.out.printf(
                    "Average Indexing Time/File: %.3f ms%n",
                    averageIndexingMs
            );

            System.out.printf(
                    "Average Search Latency: %.3f ms%n",
                    averageSearchMs
            );

            System.out.printf(
                    "Minimum Search Latency: %.3f ms%n",
                    minSearchMs
            );

            System.out.printf(
                    "Maximum Search Latency: %.3f ms%n",
                    maxSearchMs
            );

            System.out.printf(
                    "Approx. Memory Increase: %.3f MB%n",
                    memoryIncreaseMb
            );

            System.out.println(
                    "Vector Store Size: "
                            + pipeline.getVectorStore().size()
            );

            System.out.println(
                    "Search Results: "
                            + (lastResults == null
                            ? 0
                            : lastResults.size())
            );

            assertEquals(
                    fileCount,
                    pipeline.getVectorStore().size()
            );

            assertNotNull(lastResults);
            assertFalse(lastResults.isEmpty());
            assertTrue(lastResults.size() <= 10);

            for (File file : files) {
                file.delete();
            }
        }

        System.out.println();
        System.out.println("===== BASELINE BENCHMARK COMPLETED =====");
    }

    private long getUsedMemory() {

        Runtime runtime = Runtime.getRuntime();

        return runtime.totalMemory() - runtime.freeMemory();
    }
}