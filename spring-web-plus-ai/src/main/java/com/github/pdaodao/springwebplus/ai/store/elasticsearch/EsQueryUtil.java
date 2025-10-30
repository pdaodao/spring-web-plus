package com.github.pdaodao.springwebplus.ai.store.elasticsearch;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;

import java.util.ArrayList;
import java.util.List;

public class EsQueryUtil {

    /**
     * 过滤条件
     *
     * @param query
     * @return
     */
    public static Query buildFilter(final AiEmbedTextQuery query) {
        if (query == null) {
            return null;
        }
        final BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        // 团队id
        if (StrUtil.isNotBlank(query.getTeamId())) {
            final TermQuery teamId = new TermQuery.Builder()
                    .field("teamId")
                    .value(query.getTeamId())
                    .build();
            boolQuery.filter(new Query.Builder().term(teamId).build());
        }
        // 命名空间
        if (CollUtil.isNotEmpty(query.getNamespaces())) {
            final Query namespaceFilter = EsQueryUtil.equalOrIn("namespace", query.getNamespaces());
            if (namespaceFilter != null) {
                boolQuery.filter(namespaceFilter);
            }
        }
        // type
        if (CollUtil.isNotEmpty(query.getTypes())) {
            final Query typeFilter = EsQueryUtil.equalOrIn("type", query.getTypes());
            if (typeFilter != null) {
                boolQuery.filter(typeFilter);
            }
        }
        // 主题
        if (CollUtil.isNotEmpty(query.getTopics())) {
            final Query topicFilter = EsQueryUtil.equalOrIn("topic", query.getTopics());
            if (topicFilter != null) {
                boolQuery.filter(topicFilter);
            }
        }
        // 文档id
        if (CollUtil.isNotEmpty(query.getDocIds())) {
            final Query docFilter = EsQueryUtil.equalOrIn("docId", query.getDocIds());
            if (docFilter != null) {
                boolQuery.filter(docFilter);
            }
        }
        // 文本块id
        if (CollUtil.isNotEmpty(query.getTextIds())) {
            final Query textIdFilter = EsQueryUtil.equalOrIn("textId", query.getTextIds());
            if (textIdFilter != null) {
                boolQuery.filter(textIdFilter);
            }
        }

        return boolQuery.build()._toQuery();
    }


    public static Query equalOrIn(final String field, final List<String> values) {
        if (CollUtil.isEmpty(values)) {
            return null;
        }
        if (CollUtil.size(values) == 1) {
            final TermQuery namespace = new TermQuery.Builder()
                    .field(field)
                    .value(values.get(0))
                    .build();
            return new Query.Builder().term(namespace).build();
        }
        final List<FieldValue> filters = new ArrayList<>();
        for (final String obj : values) {
            if (obj == null) {
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
}
