package com.github.pdaodao.springwebplus.ai.store.elasticsearch;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnSearch;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import com.github.pdaodao.springwebplus.tool.elasticsearch.EsUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ElasticsearchVectorStore implements AiVectorStore {
    private final ElasticsearchOptions options;
    private final ElasticsearchClient client;

    public ElasticsearchVectorStore(final ElasticsearchOptions opt) throws Exception{
        this.options = opt;
        this.client = EsUtil.getClient(opt.getUrl(), opt.getUsername(), opt.getPassword());
        Preconditions.checkNotBlank(opt.getIndexName(), "Elasticsearch indexName is null.");
        init();
    }

    public ElasticsearchVectorStore(ElasticsearchClient client, final ElasticsearchOptions opt) throws Exception{
        this.options = opt;
        this.client = client;
        Preconditions.checkNotBlank(opt.getIndexName(), "Elasticsearch indexName is null.");
        init();
    }

    @Override
    public void add(List<AiEmbedText> documents) throws Exception {
        if(CollUtil.isEmpty(documents)){
            return;
        }

        final BulkRequest.Builder bulkRequest = new BulkRequest.Builder();
        bulkRequest.timeout(Time.of(f -> f.time("90s")));
        for (final AiEmbedText doc : documents) {
            bulkRequest.operations(op -> op.index(idx -> idx
                    .index(options.getIndexName())
                    .id(doc.getId())
                    .document(doc)));
        }
        // 执行批量插入
        final BulkResponse response = client.bulk(bulkRequest.build());
        if(response.errors()){
            throw new RuntimeException(response.toString());
        }
    }

    @Override
    public List<AiEmbedText> query(final AiEmbedTextQuery query) throws Exception {
        if(query.getScore() == null){
            query.setScore(0.3);
        }
        final SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder();
        searchRequestBuilder.index(options.getIndexName());
        searchRequestBuilder.size(query.getTopK());
        final Query matchQuery = buildQuery(query);
        if(query.getEmbedding() != null){
            final KnnSearch knnQuery = new KnnSearch.Builder()
                    .field("embedding")  // 向量字段
                    .queryVector(AiEmbedTextQuery.asList(query.getEmbedding()))  // 查询向量
                    .similarity(query.getScore().floatValue())
                    .k(query.getTopK())   // 取前 5 个最相似的文档
                    .filter(matchQuery)
                    .numCandidates(query.getTopK() * 2)  // 初步筛选
                    .build();
            searchRequestBuilder.knn(knnQuery);
        }else{
            searchRequestBuilder.query(matchQuery);
        }
        final SearchRequest searchRequest = searchRequestBuilder.build();
        // 执行搜索
        final SearchResponse<AiEmbedText> response = client.search(searchRequest, AiEmbedText.class);
        final List<AiEmbedText> retList = new ArrayList<>();
        for (final Hit<AiEmbedText> hit : response.hits().hits()) {
            final AiEmbedText tt = hit.source();
            tt.setScore(hit.score());
            retList.add(tt);
        }
        return retList;
    }

    private static Query buildQuery(final AiEmbedTextQuery query){
        if(query == null){
            return null;
        }
        final BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        if(StrUtil.isNotBlank(query.getNamespace())){
            final TermQuery namespace = new TermQuery.Builder()
                    .field("namespace")
                    .value(query.getNamespace())
                    .build();
            boolQuery.filter(new Query.Builder().term(namespace).build());
        }
        if(query.getEnabled() != null){
            final TermQuery enabled = new TermQuery.Builder()
                    .field("enabled")
                    .value(query.getEnabled())
                    .build();
            boolQuery.filter(new Query.Builder().term(enabled).build());
        }
        if(StrUtil.isNotBlank(query.getDocId())){
            final TermQuery docId = new TermQuery.Builder()
                    .field("docId")
                    .value(query.getDocId())
                    .build();
            boolQuery.filter(new Query.Builder().term(docId).build());
        }else{
            if(StrUtil.isNotBlank(query.getTeamId())){
                final TermQuery teamId = new TermQuery.Builder()
                        .field("teamId")
                        .value(query.getTeamId())
                        .build();
                boolQuery.filter(new Query.Builder().term(teamId).build());
            }
            if(StrUtil.isNotBlank(query.getDbId())){
                final TermQuery datasetId = new TermQuery.Builder()
                        .field("dbId")
                        .value(query.getDbId())
                        .build();
                boolQuery.filter(new Query.Builder().term(datasetId).build());
            }
        }
        if(StrUtil.isNotBlank(query.getContent())){
            if(query.getScore() < 0){
                query.setScore(0.3);
            }
            final int match = (int)(query.getScore() * 100);
            final MatchQuery matchQuery = new MatchQuery.Builder()
                    .field("content")
                    .query(query.getContent().trim())
                    .minimumShouldMatch(match+"%")
                    .analyzer("ik_max_word")
                    .build();
            boolQuery.must(new Query.Builder()
                    .match(matchQuery)
                    .build());
        }
        final Query finalQuery = new Query.Builder()
                .bool(boolQuery.build())
                .build();
        return finalQuery;
    }


    @Override
    public long count() throws Exception {
        return client.count(new CountRequest.Builder().index(options.getIndexName()).build()).count();
    }

    private void init() throws Exception{
        final boolean indexExists = client.indices().exists(e -> e.index(options.getIndexName())).value();
        if(indexExists){
            return;
        }
        final CreateIndexRequest request = new CreateIndexRequest.Builder()
                .index(options.getIndexName())
                .settings(t -> t.numberOfReplicas("0")
                        .numberOfShards("1"))
                .mappings(m -> m
                        .properties("namespace", p -> p.keyword(t -> t))
                        .properties("teamId", p -> p.keyword(t -> t))
                        .properties("dbId", p -> p.keyword(t -> t))
                        .properties("docId", p -> p.keyword(t -> t))
                        .properties("textId", p -> p.keyword(t -> t))
                        .properties("name", p -> p.keyword(t -> t))
                        .properties("content", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                        .properties("embedding", p -> p.denseVector(v -> v.dims(options.getDimensions()).index(true).similarity("cosine"))) // 向量字段
                )
                .build();
        final CreateIndexResponse response = client.indices().create(request);
        if(!response.acknowledged()){
            throw new RuntimeException(response.toString());
        }
    }
}
