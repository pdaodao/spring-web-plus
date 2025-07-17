package com.github.pdaodao.springwebplus.tool.elasticsearch.build;


import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import com.github.pdaodao.springwebplus.tool.elasticsearch.EsEscapeUtil;
import java.util.Arrays;

/**
 * 转换器 ： EsOperator 到 Elasticsearch 查询语句
 */
public abstract class EsDslBuilder extends DslBuilder<Query> {
    public static String EmptyStringShow = "-空值-";

    @Override
    protected String escape(String str) {
        return EsEscapeUtil.escape(str);
    }

    /**
     * 构造 match query
     *
     * @param text      未转义的关键词
     * @param dslConfig
     * @param fields    字段
     * @return
     */
    protected final Query matchQuery(Object text, DslConfig dslConfig, String... fields) {
        KeyContext keyContext = processKey(text, fields);
        if (keyContext == null || fields == null || StrUtil.isEmpty(keyContext.getKey())) return null;
        if (keyContext.isWith() && !fields[0].endsWith("*")) {
            return doMatchWild(keyContext, fields);
        } else {
            if (fields.length == 1 && !fields[0].endsWith("*")) {
                final MatchQuery.Builder match = new MatchQuery.Builder()
                        .field(fields[0])
                        .query(keyContext.getKey());
                if (dslConfig != null && dslConfig.getFuzzyThreshold() > 2 && keyContext.getKey().length() > dslConfig.getFuzzyThreshold()) {
                    match.minimumShouldMatch(dslConfig.getFuzzyMinMatch() + "%");
                }
                return match.build()._toQuery();
            }
            final MultiMatchQuery.Builder multiMatchQuery = new MultiMatchQuery.Builder()
                    .fields(Arrays.stream(fields).toList())
                    .query(keyContext.getKey());
            if (dslConfig != null && dslConfig.getFuzzyThreshold() > 2 && keyContext.getKey().length() > dslConfig.getFuzzyThreshold()) {
                multiMatchQuery.minimumShouldMatch(dslConfig.getFuzzyMinMatch() + "%");
            }
            return multiMatchQuery.build()._toQuery();
        }
    }

    protected final Query doMatchWild(KeyContext keyContext, String... fields) {
        final String key = keyContext.getKey();
        if (fields.length == 1) {
            return new WildcardQuery.Builder()
                    .field(fields[0])
                    .value(key)
                    .build()._toQuery();
        } else {
            final BoolQuery.Builder boolQuery = new BoolQuery.Builder();
            for(final String f: fields){
                boolQuery.should(new WildcardQuery.Builder().field(f).value(key).build()._toQuery());
            }
            return boolQuery.build()._toQuery();
        }
    }

    /**
     * match phrase
     *
     * @param text
     * @param dslConfig
     * @param fields
     * @return
     */
    protected final Query matchPhraseQuery(Object text, DslConfig dslConfig, String... fields) {
        KeyContext keyContext = processKey(text, fields);
        if (keyContext == null || fields == null) return null;
        if (keyContext.isWith() && !fields[0].endsWith("*")) {
            return doMatchWild(keyContext, fields);
        } else {
            int slop = 0;
            if (StrUtil.isNotEmpty(keyContext.getKey()) &&
                    dslConfig.getAccThreshold() > 2 &&
                    keyContext.getKey().length() > dslConfig.getAccThreshold()) {
                slop = dslConfig.getAccPhraseSlop();
            }
            if (fields.length == 1 && !fields[0].endsWith("*")) {
                final MatchPhraseQuery.Builder builder = new MatchPhraseQuery.Builder()
                        .field(fields[0])
                        .query(keyContext.getKey());
                if(slop > 0){
                    builder.slop(slop);
                }
                return new Query.Builder().matchPhrase(builder.build()) .build();
            }
            final MultiMatchQuery.Builder multi = new MultiMatchQuery
                    .Builder()
                    .fields(Arrays.stream(fields).toList())
                    .query(keyContext.getKey());
            if(slop > 0){
                multi.slop(slop);
            }
            return new Query.Builder().multiMatch(multi.build()).build();
        }
    }

}
