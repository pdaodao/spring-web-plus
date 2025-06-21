package com.github.pdaodao.springwebplus.ai.store.elasticsearch;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnSearch;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.mapping.DynamicMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.github.pdaodao.springwebplus.ai.core.DocText;
import com.github.pdaodao.springwebplus.ai.core.DocTextQuery;
import com.github.pdaodao.springwebplus.ai.store.DocEmbeddingUtil;
import com.github.pdaodao.springwebplus.ai.store.DocVectorStore;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class EsDocVectorStore implements DocVectorStore {
    private final ElasticsearchClient client;

    @Override
    public void init() throws Exception{
        final boolean indexExists = client.indices().exists(e -> e.index(EsUtil.ChatIndexName)).value();
        if(indexExists){
            return;
        }
        final CreateIndexRequest request = new CreateIndexRequest.Builder()
                .index(EsUtil.ChatIndexName)  // 索引名称
                .settings(s -> s
                        .numberOfShards("1")  // 分片数
                        .numberOfReplicas("0") // 副本数
                )
                .mappings(m -> m
                        .dynamic(DynamicMapping.False)
                        .properties("docId", p -> p.long_(t -> t))
                        .properties("datasetId", p -> p.long_(t -> t))
                        .properties("teamId", p -> p.long_(t -> t))
                        .properties("namespace", p -> p.keyword(t -> t))
                        .properties("title", p -> p.keyword(t -> t))
                        .properties("content", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                        .properties("embedding", p -> p.denseVector(v -> v.dims(1536).index(true).similarity("cosine"))) // 向量字段
                )
                .build();
        final CreateIndexResponse response = client.indices().create(request);
        if(!response.acknowledged()){
            throw new RuntimeException(response.toString());
        }
    }

    @Override
    public void save(List<DocText> docTextList) throws Exception {
        final BulkRequest.Builder bulkRequest = new BulkRequest.Builder();
        bulkRequest.timeout(Time.of(f -> f.time("90s")));
        for (final DocText doc : docTextList) {
            bulkRequest.operations(op -> op.index(idx -> idx
                    .index(EsUtil.ChatIndexName)
                    .id(doc.id())
                    .document(doc)) );
        }
        // 执行批量插入
        final BulkResponse response = client.bulk(bulkRequest.build());
        if(response.errors()){
            throw new RuntimeException(response.toString());
        }
    }

    @Override
    public void delete(final List<String> ids) throws Exception{
        final BulkRequest.Builder bulkRequest = new BulkRequest.Builder();
        for(final String id: ids){
            bulkRequest.operations(op -> op
                    .delete(idx -> idx.index(EsUtil.ChatIndexName)
                            .id(id))
            );
        }
        final BulkResponse response = client.bulk(bulkRequest.build());
        if(response.errors()){
            throw new RuntimeException(response.toString());
        }
    }

    @Override
    public List<DocText> query(final DocTextQuery docText) throws Exception{
        final BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        if(ObjectUtil.isNotNull(docText.getDocId())){
            final TermQuery docId = new TermQuery.Builder()
                    .field("docId")
                    .value(docText.getDocId())
                    .build();
            boolQuery.filter(new Query.Builder().term(docId).build());
        }else{
            if(ObjectUtil.isNotNull(docText.getTeamId())){
                final TermQuery teamId = new TermQuery.Builder()
                        .field("teamId")
                        .value(docText.getTeamId())
                        .build();
                boolQuery.filter(new Query.Builder().term(teamId).build());
            }
            if(ObjectUtil.isNotNull(docText.getNamespace())){
                final TermQuery teamId = new TermQuery.Builder()
                        .field("namespace")
                        .value(docText.getNamespace())
                        .build();
                boolQuery.filter(new Query.Builder().term(teamId).build());
            }

            if(ObjectUtil.isNotNull(docText.getDatasetId())){
                final TermQuery teamId = new TermQuery.Builder()
                        .field("datasetId")
                        .value(docText.getDatasetId())
                        .build();
                boolQuery.filter(new Query.Builder().term(teamId).build());
            }
        }
        if(StrUtil.isNotBlank(docText.getContent())){
            final int match = (int)(docText.getMinScore() * 100) - 5;
            final MatchQuery matchQuery = new MatchQuery.Builder()
                    .field("content")
                    .query(docText.getContent())
                    .minimumShouldMatch(match+"%")
                    .analyzer("ik_max_word")
                    .build();
            boolQuery.must(new Query.Builder()
                    .match(matchQuery)
                    .build());
        }

        // 构造 SearchRequest
        final SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder();
        searchRequestBuilder.index(EsUtil.ChatIndexName);
        searchRequestBuilder.size(docText.getTopK());
        if(docText.getEmbedding() != null){
            final KnnSearch knnQuery = new KnnSearch.Builder()
                    .field("embedding")  // 向量字段
                    .queryVector(DocEmbeddingUtil.asList(docText.getEmbedding()))  // 查询向量
                    .similarity(docText.getMinScore())
                    .k(docText.getTopK())  // 取前 5 个最相似的文档
                    .filter(new Query.Builder().bool(boolQuery.build()).build())
                    .numCandidates(docText.getTopK() * 3)  // 初步筛选
                    .build();
            searchRequestBuilder.knn(knnQuery);
        }else{
            final Query finalQuery = new Query.Builder()
                    .bool(boolQuery.build())
                    .build();
            searchRequestBuilder.query(finalQuery);
        }
        final SearchRequest searchRequest = searchRequestBuilder.build();
        // 执行搜索
        final SearchResponse<DocText> response = client.search(searchRequest, DocText.class);
        final List<DocText> retList = new ArrayList<>();
        for (final Hit<DocText> hit : response.hits().hits()) {
            final DocText tt = hit.source();
            tt.setScore(hit.score());
            retList.add(tt);
        }
        return retList;
    }

    public long count() throws Exception{
        return client.count(new CountRequest.Builder().index(EsUtil.ChatIndexName).build()).count();
    }
}