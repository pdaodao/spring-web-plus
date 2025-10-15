package com.github.pdaodao.springwebplus.ai.config;

import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.store.OpenAiEmbedding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = AiAutoConfig.class)
@ConditionalOnProperty("ai.embedding.baseUrl")
public class AiEmbeddingAutoConfig {
    @Value("${ai.embedding.baseUrl:}")
    private String baseUrl;

    @Value("${ai.embedding.apiKey:}")
    private String apiKey;


    @Value("${ai.embedding.model:}")
    private String model;


    @Bean
    public AiEmbedding aiEmbedding(){
        return new OpenAiEmbedding(baseUrl, apiKey, model);
    }
}