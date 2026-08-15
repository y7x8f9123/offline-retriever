package com.offlineretriever.vector;

public class VectorRecord {

    private String id;
    private String fileName;
    private String filePath;
    private float[] embedding;

    // New constructor with file path
    public VectorRecord(
            String id,
            String fileName,
            String filePath,
            float[] embedding
    ) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.embedding = embedding;
    }

    // Backward-compatible constructor for existing tests/code
    public VectorRecord(
            String id,
            String fileName,
            float[] embedding
    ) {
        this(id, fileName, null, embedding);
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    @Override
    public String toString() {
        return "VectorRecord{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", embeddingLength=" +
                (embedding == null ? 0 : embedding.length) +
                '}';
    }
}