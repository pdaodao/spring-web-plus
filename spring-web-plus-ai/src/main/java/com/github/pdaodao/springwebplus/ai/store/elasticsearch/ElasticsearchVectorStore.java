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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public void add(List<AiEmbedText> documents) throws Exception {
        if (CollUtil.isEmpty(documents)) {
            return;
        }
        if (aiEmbedding.isPresent()) {
            final List<String> contentList = documents.stream()
                    .filter(t -> StrUtil.isNotBlank(t.getContent()))
                    .filter(t -> ArrayUtil.isEmpty(t.getEmbedding()))
                    .map(t -> t.getContent())
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(contentList)) {
                final List<float[]> floatList = aiEmbedding.get().embed(contentList);
                int index = 0;
                for (final AiEmbedText d : documents) {
                    if (StrUtil.isBlank(d.getContent()) || ArrayUtil.isNotEmpty(d.getEmbedding())) {
                        continue;
                    }
                    d.setEmbedding(floatList.get(index++));
                }
            }
        }
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
        searchRequestBuilder.minScore(query.getScore());

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
                        .properties("topic", p -> p.keyword(t -> t))
                        .properties("docId", p -> p.keyword(t -> t))
                        .properties("textId", p -> p.keyword(t -> t))
                        .properties("type", p -> p.keyword(t -> t))
                        .properties("name", p -> p.keyword(t -> t))
                        .properties("title", p -> p.keyword(t -> t))
                        .properties("content", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_max_word")))
                        .properties("embedding", p -> p.denseVector(v -> v.dims(options.getDimensions()).index(true).similarity("cosine"))) // 向量字段
                )
                .build();
        final CreateIndexResponse response = client.indices().create(request);
        if (!response.acknowledged()) {
            throw new RuntimeException(response.toString());
        }
    }
}