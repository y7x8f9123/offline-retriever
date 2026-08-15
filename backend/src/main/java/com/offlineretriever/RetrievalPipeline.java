package com.offlineretriever;

import com.offlineretriever.embedding.TextEmbeddingEngine;
import com.offlineretriever.factory.ParserFactory;
import com.offlineretriever.parser.Parser;
import com.offlineretriever.vector.Retriever;
import com.offlineretriever.vector.SearchResult;
import com.offlineretriever.vector.VectorRecord;
import com.offlineretriever.vector.VectorStore;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RetrievalPipeline {

    private final TextEmbeddingEngine embeddingEngine;
    private final VectorStore vectorStore;
    private final Retriever retriever;

    public RetrievalPipeline() {
        embeddingEngine = new TextEmbeddingEngine();
        vectorStore = new VectorStore();
        retriever = new Retriever(vectorStore);
    }

    /**
     * Index a supported local file into the vector store.
     */
    public void indexFile(File file) throws IOException {

        Parser parser = ParserFactory.getParser(file.getName());

        if (parser == null) {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + file.getName()
            );
        }

        String content = parser.parse(file.getAbsolutePath());

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "No readable content found in file: " + file.getName()
            );
        }

        if (content.startsWith("Error reading document:")) {
            throw new IOException(content);
        }

        float[] embedding = embeddingEngine.embed(content);

        VectorRecord record = new VectorRecord(
                file.getName(),
                file.getName(),
                file.getAbsolutePath(),
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