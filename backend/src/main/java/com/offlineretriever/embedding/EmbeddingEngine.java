package com.offlineretriever.embedding;

public interface EmbeddingEngine<T> {

    float[] embed(T input);

}