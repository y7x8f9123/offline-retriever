package com.offlineretriever;

import com.offlineretriever.embedding.TextEmbeddingEngine;
import com.offlineretriever.parser.TextParser;
import com.offlineretriever.vector.Retriever;
import com.offlineretriever.vector.SearchResult;
import com.offlineretriever.vector.VectorRecord;
import com.offlineretriever.vector.VectorStore;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RetrievalPipeline {

    private final TextParser parser;
    private final TextEmbeddingEngine embeddingEngine;
    private final VectorStore vectorStore;
    private final Retriever retriever;

    public RetrievalPipeline() {
        parser = new TextParser();
        embeddingEngine = new TextEmbeddingEngine();
        vectorStore = new VectorStore();
        retriever = new Retriever(vectorStore);
    }

    /**
     * Index a text file into the vector store.
     */
    public void indexFile(File file) throws IOException {

        String content = parser.parse(file.getAbsolutePath());

        float[] embedding = embeddingEngine.embed(content);

        VectorRecord record = new VectorRecord(
                file.getName(),
                file.getName(),
                embedding
        );

        vectorStore.add(record);
    }

    /**
     * Search indexed files.
     */
    public List<SearchResult> search(String query, int topK) {

        float[] queryVector = embeddingEngine.embed(query);

        return retriever.search(queryVector, topK);
    }

    public VectorStore getVectorStore() {
        return vectorStore;
    }
}