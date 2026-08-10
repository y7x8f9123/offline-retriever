package com.offlineretriever;

import com.offlineretriever.vector.SearchResult;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.junit.Assert.*;

public class RetrievalPipelineTest {

    @Test
    public void testFullRetrievalPipeline() throws Exception {

        File testFile = File.createTempFile("pipeline-test", ".txt");

        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(
                    "This document contains information about offline retrieval."
            );
        }

        RetrievalPipeline pipeline = new RetrievalPipeline();

        pipeline.indexFile(testFile);

        List<SearchResult> results =
                pipeline.search("offline retrieval", 1);

        assertEquals(1, pipeline.getVectorStore().size());
        assertFalse(results.isEmpty());
        assertEquals(
                testFile.getName(),
                results.get(0).getRecord().getFileName()
        );

        testFile.delete();
    }

    @Test
    public void testMultipleFilesCanBeIndexedAndSearched() throws Exception {

        File file1 = File.createTempFile("retrieval-document", ".txt");
        File file2 = File.createTempFile("network-document", ".txt");
        File file3 = File.createTempFile("database-document", ".txt");

        try (FileWriter writer = new FileWriter(file1)) {
            writer.write(
                    "Offline semantic retrieval searches local documents."
            );
        }

        try (FileWriter writer = new FileWriter(file2)) {
            writer.write(
                    "Computer networks use protocols for communication."
            );
        }

        try (FileWriter writer = new FileWriter(file3)) {
            writer.write(
                    "Database systems store and manage structured data."
            );
        }

        RetrievalPipeline pipeline = new RetrievalPipeline();

        pipeline.indexFile(file1);
        pipeline.indexFile(file2);
        pipeline.indexFile(file3);

        assertEquals(3, pipeline.getVectorStore().size());

        List<SearchResult> results =
                pipeline.search("offline document retrieval", 2);

        assertFalse(results.isEmpty());
        assertTrue(results.size() <= 2);

        file1.delete();
        file2.delete();
        file3.delete();
    }

    @Test
    public void testTopKLimitsNumberOfResults() throws Exception {

        RetrievalPipeline pipeline = new RetrievalPipeline();

        File file1 = createTextFile("alpha document");
        File file2 = createTextFile("beta document");
        File file3 = createTextFile("gamma document");

        pipeline.indexFile(file1);
        pipeline.indexFile(file2);
        pipeline.indexFile(file3);

        List<SearchResult> results =
                pipeline.search("document", 2);

        assertEquals(2, results.size());

        file1.delete();
        file2.delete();
        file3.delete();
    }

    @Test
    public void testSearchOnEmptyStoreReturnsNoResults() {

        RetrievalPipeline pipeline = new RetrievalPipeline();

        List<SearchResult> results =
                pipeline.search("offline retrieval", 5);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    private File createTextFile(String content) throws Exception {

        File file = File.createTempFile("pipeline-", ".txt");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }

        return file;
    }
}