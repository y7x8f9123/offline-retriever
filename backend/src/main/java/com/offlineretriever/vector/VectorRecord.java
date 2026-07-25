package com.offlineretriever.vector;

public class VectorRecord {

    private String id;
    private String fileName;
    private float[] embedding;

    public VectorRecord(String id, String fileName, float[] embedding) {
        this.id = id;
        this.fileName = fileName;
        this.embedding = embedding;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
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

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    @Override
    public String toString() {
        return "VectorRecord{" +
                "id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", embeddingLength=" + (embedding == null ? 0 : embedding.length) +
                '}';
    }
}