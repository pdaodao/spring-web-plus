package com.github.pdaodao.springwebplus.tool.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import co.elastic.clients.elasticsearch.indices.IndexState;
import com.github.pdaodao.springwebplus.tool.table.TableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EsTemplate {
    private final ElasticsearchClient client;

    public EsTemplate(ElasticsearchClient client) {
        this.client = client;
    }

    public List<TableInfo> list() throws Exception{
        final List<TableInfo> list = new ArrayList<>();
        final Map<String, IndexState> map =  client.indices().get(new GetIndexRequest.Builder().build()).result();
        for(final Map.Entry<String, IndexState> entry: map.entrySet()){
            final IndexState stat = entry.getValue();
            final TableInfo info = new TableInfo();
            info.setName(entry.getKey());
            list.add(info);
        }
        return list;
    }

    public long count(final String indexName) throws Exception {
        return client.count(new CountRequest.Builder().index(indexName).build()).count();
    }

    public boolean exist(final String indexName) throws Exception{
        return client.indices().exists(e -> e.index(indexName)).value();
    }
}