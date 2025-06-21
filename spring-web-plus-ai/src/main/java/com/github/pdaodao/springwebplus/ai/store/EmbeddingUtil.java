package com.github.pdaodao.springwebplus.ai.store;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.PoolingMode;

public class EmbeddingUtil {
    public static EmbeddingModel embeddingModel;

    public static float[] embeding(final String text) {
        return model().embed(text).content().vector();
    }

    public static EmbeddingModel model() {
        if (embeddingModel != null) {
            return embeddingModel;
        }
        String pathToModel = "/Users/peng/Workspace/work2024/spring-web-plus/spring-web-plus-ai/docs/model_quantized.onnx";
        String pathToTokenizer = "/Users/peng/Workspace/work2024/spring-web-plus/spring-web-plus-ai/docs/tokenizer.json";
        PoolingMode poolingMode = PoolingMode.MEAN;
        embeddingModel = new OnnxEmbeddingModel(pathToModel, pathToTokenizer, poolingMode);
        return embeddingModel;
    }
}
