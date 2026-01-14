package com.github.pdaodao.springwebplus.ai.store.elasticsearch;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnSearch;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import com.github.pdaodao.springwebplus.tool.elasticsearch.EsUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import java.util.*;

/**
 * 基于Elasticsearch 的向量存储
 */
@AllArgsConstructor
public class ElasticsearchVectorStore implements AiVectorStore {
    private final ElasticsearchOptions options;
    private final ElasticsearchClient client;
    private final Optional<AiEmbedding> aiEmbedding;

    public ElasticsearchVectorStore(final ElasticsearchOptions opt, final Optional<AiEmbedding> aiEmbedding) throws Exception {
        this.options = opt;
        this.aiEmbedding = aiEmbedding;
        this.client = EsUtil.getClient(opt.getUrl(), opt.getUsername(), opt.getPassword());
        Preconditions.checkNotBlank(opt.getIndexName(), "Elasticsearch indexName is null.");
        init();
    }

    public ElasticsearchVectorStore(ElasticsearchClient client,
                                    final ElasticsearchOptions opt,
                                    final Optional<AiEmbedding> aiEmbedding) throws Exception {
        this.options = opt;
        this.client = client;
        this.aiEmbedding = aiEmbedding;
        Preconditions.checkNotBlank(opt.getIndexName(), "Elasticsearch indexName is null.");
        init();
    }

    private void checkNamespace(final List<AiEmbedText> documents){
        for(final AiEmbedText t: documents){
            Preconditions.checkNotBlank(t.getNamespace(), "namespace不能为空:"+t.getContent());
        }
    }

    @Override
    public void save(final List<AiEmbedText> documents) throws Exception {
        if (CollUtil.isEmpty(documents)) {
            return;
        }
        checkNamespace(documents);
        // 旧数据的查询条件
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        for(final AiEmbedText t: documents){
            query.addNamespace(t.getNamespace());
            query.setTeamId(t.getTeamId());
            query.addTopic(t.getTopic());
            query.addDocId(t.getDocId());
            query.addType(t.getType());
            query.addId(t.getId());
        }
        final Query esFilter = EsQueryUtil.buildFilter(query);
        if (aiEmbedding != null && aiEmbedding.isPresent()) {
            //1. 先获取旧数据 节省向量化资源
            final Map<String, float[]> floatMap = new HashMap<>();
            final SearchResponse<AiEmbedText> response = client.search(new SearchRequest.Builder()
                    .index(options.getIndexName())
                    .query(esFilter)
                    .build(), AiEmbedText.class);
            for (final Hit<AiEmbedText> hit : response.hits().hits()) {
                final AiEmbedText tt = hit.source();
                if(StrUtil.isNotBlank(tt.getContent()) && tt.getEmbedding() != null){
                    floatMap.put(tt.getContent(), tt.getEmbedding());
                }
            }
            //2. 待向量化的文本
            final List<String> toEmbedTexts = new ArrayList<>();
            for(final AiEmbedText t: documents){
                if(StrUtil.isNotBlank(t.getContent()) &&
                        ArrayUtil.isEmpty(t.getEmbedding()) &&
                        !floatMap.containsKey(t.getContent())){
                    toEmbedTexts.add(t.getContent());
                }
            }
            //3. 进行向量化
            if(CollUtil.isNotEmpty(toEmbedTexts)){
                final List<float[]> floatList = aiEmbedding.get().embed(toEmbedTexts);
                int index = 0;
                for(final String t: toEmbedTexts){
                    floatMap.put(t, floatList.get(index++));
                }
            }
            for(final AiEmbedText t: documents){
                if(StrUtil.isBlank(t.getContent()) || ArrayUtil.isNotEmpty(t.getEmbedding())){
                    continue;
                }
                t.setEmbedding(floatMap.get(t.getContent()));
            }
        }
        // 删除旧数据
        deleteByQuery(query);
        // 插入新数据
        final BulkRequest.Builder bulkRequest = new BulkRequest.Builder();
        bulkRequest.timeout(Time.of(f -> f.time("90s")));
        for (final AiEmbedText doc : documents) {
            bulkRequest.operations(op -> op.index(idx -> idx
                    .index(options.getIndexName())
                    .document(doc)));
        }
        // 执行批量插入
        final BulkResponse response = client.bulk(bulkRequest.build());
        if (response.errors()) {
            throw new RuntimeException(response.toString());
        }
    }

    @Override
    public List<AiEmbedText> query(final AiEmbedTextQuery query) throws Exception {
        final SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder();
        searchRequestBuilder.index(options.getIndexName());
        searchRequestBuilder.size(query.getTopK());
        if(query.getScore() != null && query.getScore() > 0){
            searchRequestBuilder.minScore(query.getScore());
        }
        final Query filterQuery = EsQueryUtil.buildFilter(query);
        final BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        boolQuery.filter(filterQuery);

        if (StrUtil.isNotBlank(query.getContent())) {
            if (aiEmbedding.isPresent() && query.getEmbedding() == null) {
                query.setEmbedding(aiEmbedding.get().embed(query.getContent()));
                final KnnSearch knnQuery = new KnnSearch.Builder()
                        .field("embedding")  // 向量字段
                        .similarity(0.1f)
                        .queryVector(AiEmbedTextQuery.asList(query.getEmbedding()))  // 查询向量
                        .k(query.getTopK() * 5)
//                .filter(filterQuery)
                        .numCandidates(query.getTopK() * 50)  // 初步筛选
                        .build();
                searchRequestBuilder.knn(knnQuery);
            }
            final MatchQuery matchQuery = new MatchQuery.Builder()
                    .field("content")
                    .query(query.getContent().trim())
                    .analyzer("ik_max_word")
                    .build();
            boolQuery.must(matchQuery._toQuery());
        }
        searchRequestBuilder.query(boolQuery.build()._toQuery());
        // 执行搜索
        final SearchResponse<AiEmbedText> response = client.search(searchRequestBuilder.build(), AiEmbedText.class);
        final List<AiEmbedText> retList = new ArrayList<>();
        for (final Hit<AiEmbedText> hit : response.hits().hits()) {
            final AiEmbedText tt = hit.source();
            tt.setScore(hit.score());
            retList.add(tt);
        }
        return retList;
    }


    @Override
    public long count() throws Exception {
        return client.count(new CountRequest.Builder().index(options.getIndexName()).build()).count();
    }

    @Override
    public Long deleteByQuery(final AiEmbedTextQuery query) throws Exception {
        final Query q = EsQueryUtil.buildFilter(query);
        final DeleteByQueryResponse response = client.deleteByQuery(new DeleteByQueryRequest.Builder()
                .index(options.getIndexName())
                .query(q).build());
        return response.deleted();
    }

    private void init() throws Exception {
        final boolean indexExists = client.indices().exists(e -> e.index(options.getIndexName())).value();
        if (indexExists) {
            return;
        }
        final CreateIndexRequest request = new CreateIndexRequest.Builder()
                .index(options.getIndexName())
                .settings(t -> t.numberOfReplicas(StrUtil.toString(options.getNumberOfReplicas()))
                        .numberOfShards(StrUtil.toString(options.getNumberOfShards())))
                .mappings(m -> m
                        .properties("namespace", p -> p.keyword(t -> t))
                        .properties("teamId", p -> p.keyword(t -> t))
                        .properties("id", p -> p.keyword(t -> t))
                        .properties("type", p -> p.keyword(t -> t))
                        .properties("topic", p -> p.keyword(t -> t))
                        .properties("docId", p -> p.keyword(t -> t))
                        .properties("name", p -> p.keyword(t -> t))
                        .properties("title", p -> p.keyword(t -> t))
                        .properties("content", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                        .properties("embedding", p -> p.denseVector(v -> v.dims(options.getDimensions()).index(true).similarity("cosine"))) // 向量字段
                        .properties("meta", p -> p.object(o -> o))
                )
                .build();
        final CreateIndexResponse response = client.indices().create(request);
        if (!response.acknowledged()) {
            throw new RuntimeException(response.toString());
        }
    }
}