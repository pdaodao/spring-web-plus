package com.github.pdaodao.springwebplus.ai.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.store.ElasticsearchClientService;
import com.github.pdaodao.springwebplus.ai.store.elasticsearch.ElasticsearchOptions;
import com.github.pdaodao.springwebplus.ai.store.elasticsearch.ElasticsearchVectorStore;
import com.github.pdaodao.springwebplus.tool.elasticsearch.EsUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@AutoConfiguration(after = AiEmbeddingAutoConfig.class)
@ConditionalOnProperty("ai.elasticsearch.url")
@EnableConfigurationProperties(ElasticsearchOptions.class)
public class ElasticsearchStoreAutoConfig {
    @Bean
    public ElasticsearchClientService elasticsearchClientService(final ElasticsearchOptions opt) {
        final ElasticsearchClient client = EsUtil.getClient(opt.getUrl(), opt.getUsername(), opt.getPassword());
        return new ElasticsearchClientService(client);
    }

    @Bean
    public AiVectorStore aiVectorStore(final ElasticsearchOptions options, final ElasticsearchClientService clientService, final Optional<AiEmbedding> aiEmbedding) throws Exception {
        if (aiEmbedding.isPresent()) {
            options.setDimensions(aiEmbedding.get().dimension());
        }
        final ElasticsearchVectorStore store = new ElasticsearchVectorStore(clientService.getClient(), options, aiEmbedding);
        return store;
    }
}
