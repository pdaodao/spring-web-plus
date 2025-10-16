package com.github.pdaodao.springwebplus.ai.store.elasticsearch;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
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

    public ElasticsearchVectorStore(final ElasticsearchOptions opt, final Optional<AiEmbedding> aiEmbedding) throws Exception{
        this.options = opt;
        this.aiEmbedding = aiEmbedding;
        this.client = EsUtil.getClient(opt.getUrl(), opt.getUsername(), opt.getPassword());
        Preconditions.checkNotBlank(opt.getIndexName(), "Elasticsearch indexName is null.");
        init();
    }

    public ElasticsearchVectorStore(ElasticsearchClient client,
                                    final ElasticsearchOptions opt,
                                    final Optional<AiEmbedding> aiEmbedding) throws Exception{
        this.options = opt;
        this.client = client;
        this.aiEmbedding = aiEmbedding;
        Preconditions.checkNotBlank(opt.getIndexName(), "Elasticsearch indexName is null.");
        init();
    }

    @Override
    public void add(List<AiEmbedText> documents) throws Exception {
        if(CollUtil.isEmpty(documents)){
            return;
        }
        if(aiEmbedding.isPresent()){
            final List<String> contentList = documents.stream().map(t -> t.getContent()).collect(Collectors.toList());
            final List<float[]> floatList = aiEmbedding.get().embed(contentList);
            int index = 0;
            for(final AiEmbedText d: documents){
                d.setEmbedding(floatList.get(index++));
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
        if(aiEmbedding.isPresent() && StrUtil.isNotBlank(query.getContent()) && query.getEmbedding() == null){
            query.setEmbedding(aiEmbedding.get().embed(query.getContent()));
        }
        if(query.getEmbedding() != null){
            final KnnSearch knnQuery = new KnnSearch.Builder()
                    .field("embedding")  // 向量字段
                    .queryVector(AiEmbedTextQuery.asList(query.getEmbedding()))  // 查询向量
                    .k(query.getTopK() * 5)
                    .numCandidates(query.getTopK() * 50)  // 初步筛选
                    .build();
            searchRequestBuilder.query(matchQuery)
                    .knn(knnQuery)
//                    .rank(r -> r.rrf(new RrfRank.Builder().build()))
                    .minScore(query.getScore());
        }else{
            searchRequestBuilder.query(matchQuery)
                    .minScore(query.getScore());
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

    private static Query equalOrIn(final String field, final List<String> values){
        if(CollUtil.isEmpty(values)){
            return null;
        }
        if(CollUtil.size(values) == 1){
            final TermQuery namespace = new TermQuery.Builder()
                    .field(field)
                    .value(values.get(0))
                    .build();
            return new Query.Builder().term(namespace).build();
        }
        final List<FieldValue> filters = new ArrayList<>();
        for(final String obj: values){
            if(obj == null){
                continue;
            }
            filters.add(FieldValue.of(obj));
        }
        final TermsQuery termsQuery = new TermsQuery.Builder()
                .field(field)
                .terms(new TermsQueryField.Builder().value(filters).build())
                .build();
        return termsQuery._toQuery();
    }

    private static Query buildQuery(final AiEmbedTextQuery query){
        if(query == null){
            return null;
        }
        final BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        // 团队id
        if(StrUtil.isNotBlank(query.getTeamId())){
            final TermQuery teamId = new TermQuery.Builder()
                    .field("teamId")
                    .value(query.getTeamId())
                    .build();
            boolQuery.filter(new Query.Builder().term(teamId).build());
        }
        // 命名空间
        if(CollUtil.isNotEmpty(query.getNamespaces())){
            final Query namespaceFilter = equalOrIn("namespace", query.getNamespaces());
            if(namespaceFilter != null){
                boolQuery.filter(namespaceFilter);
            }
        }
        // type
        if(CollUtil.isNotEmpty(query.getTypes())){
            final Query typeFilter = equalOrIn("type", query.getTypes());
            if(typeFilter != null){
                boolQuery.filter(typeFilter);
            }
        }
        // 主题
        if(CollUtil.isNotEmpty(query.getTopics())){
            final Query topicFilter = equalOrIn("topic", query.getTopics());
            if(topicFilter != null){
                boolQuery.filter(topicFilter);
            }
        }
        // 文档id
        if(CollUtil.isNotEmpty(query.getDocIds())){
            final Query docFilter = equalOrIn("docId", query.getDocIds());
            if(docFilter != null){
                boolQuery.filter(docFilter);
            }
        }
        // 文本块id
        if(CollUtil.isNotEmpty(query.getTextIds())){
            final Query textIdFilter = equalOrIn("textId", query.getTextIds());
            if(textIdFilter != null){
                boolQuery.filter(textIdFilter);
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
//                    .minimumShouldMatch(match+"%")
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

    @Override
    public Long deleteByQuery(final AiEmbedTextQuery query) throws Exception {
        final Query q = buildQuery(query);
        final DeleteByQueryResponse response = client.deleteByQuery(new DeleteByQueryRequest.Builder()
                .index(options.getIndexName())
                .query(q).build());
        return response.deleted();
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
        if(!response.acknowledged()){
            throw new RuntimeException(response.toString());
        }
    }
}
