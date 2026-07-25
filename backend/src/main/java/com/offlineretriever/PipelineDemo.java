package com.offlineretriever;

import com.offlineretriever.vector.SearchResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PipelineDemo {

    public static void main(String[] args) throws IOException {

        RetrievalPipeline pipeline = new RetrievalPipeline();

        File sampleFile = new File("sample.txt");

        pipeline.indexFile(sampleFile);

        List<SearchResult> results = pipeline.search(
                "sample text",
                1
        );

        System.out.println("Indexed files: "
                + pipeline.getVectorStore().size());

        System.out.println("Search results:");

        for (SearchResult result : results) {
            System.out.println(
                    result.getRecord().getFileName()
                            + " | similarity = "
                            + result.getSimilarityScore()
            );
        }
    }
}