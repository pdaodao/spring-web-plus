package com.github.pdaodao.springwebplus.ai.store.elasticsearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ai.elasticsearch")
public class ElasticsearchOptions {
    private String url;
    private String username;
    private String password;

    private String indexName = "zt_chat";

    private int dimensions = 1536;

    private Integer numberOfShards = 1;
    private Integer numberOfReplicas = 0;

    public static ElasticsearchOptions of(final String url, final String indexName) {
        final ElasticsearchOptions opt = new ElasticsearchOptions();
        opt.setUrl(url);
        opt.setIndexName(indexName);
        return opt;
    }
}
