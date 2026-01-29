package com.github.pdaodao.springwebplus.ai.config;

import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.core.OpenAiEmbedding;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Data
@AutoConfiguration(after = AiAutoConfig.class)
@ConditionalOnProperty("ai.embedding.baseUrl")
public class AiEmbeddingAutoConfig {
    @Value("${ai.embedding.baseUrl:}")
    private String baseUrl;

    @Value("${ai.embedding.apiKey:}")
    private String apiKey;

    @Value("${ai.embedding.model:}")
    private String model;

    @Value("${ai.embedding.dimension:1024}")
    private Integer dimension;

    @Value("${ai.embedding.batchSize:8}")
    private Integer batchSize;

    @Bean
    public AiEmbedding aiEmbedding() {
        return new OpenAiEmbedding(baseUrl, apiKey, model, dimension, batchSize);
    }
}