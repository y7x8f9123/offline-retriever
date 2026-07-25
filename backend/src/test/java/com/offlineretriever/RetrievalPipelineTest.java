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
            writer.write("This document contains information about offline retrieval.");
        }

        RetrievalPipeline pipeline = new RetrievalPipeline();

        pipeline.indexFile(testFile);

        List<SearchResult> results =
                pipeline.search("offline retrieval", 1);

        assertEquals(1, pipeline.getVectorStore().size());
        assertFalse(results.isEmpty());
        assertEquals(testFile.getName(),
                results.get(0).getRecord().getFileName());

        testFile.delete();
    }
}