package com.github.pdaodao.springwebplus.ai.store.elasticsearch;

import lombok.Data;

@Data
public class ElasticsearchOptions {
    private String url;
    private String username;
    private String password;

    private String indexName;

    private int dimensions = 1536;

    public static ElasticsearchOptions of(final String url, final String indexName){
        final ElasticsearchOptions opt = new ElasticsearchOptions();
        opt.setUrl(url);
        opt.setIndexName(indexName);
        return opt;
    }
}
