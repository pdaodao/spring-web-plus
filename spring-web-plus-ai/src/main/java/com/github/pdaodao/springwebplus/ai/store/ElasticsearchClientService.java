package com.github.pdaodao.springwebplus.ai.store;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ElasticsearchClientService {
    private final ElasticsearchClient client;

    public ElasticsearchClient getClient() {
        return client;
    }
}