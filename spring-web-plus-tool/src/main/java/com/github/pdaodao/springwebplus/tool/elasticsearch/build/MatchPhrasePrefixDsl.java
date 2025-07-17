package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhrasePrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.Arrays;
import java.util.List;

/**
 * match_phrase_prefix(text, 'f1', 'f2'...)
 */
public class MatchPhrasePrefixDsl extends EsDslBuilder {

    public static final MatchPhrasePrefixDsl instance = new MatchPhrasePrefixDsl();

    private MatchPhrasePrefixDsl() {
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        checkMoreThanOneArg("match_phrase_prefix", args);
        String[] fields = getFieldsFromArgs(args, dslConfig);
        if (fields.length == 1) {
            return new Query.Builder()
                    .matchPhrasePrefix(new MatchPhrasePrefixQuery.Builder()
                            .field(field)
                            .query(ObjectUtil.toString(args.get(0)))
                            .build())
                    .build();
        }
        return new Query.Builder()
                .multiMatch(new MultiMatchQuery.Builder()
                        .query(StrUtil.toString(args.get(0)))
                        .fields(Arrays.stream(fields).toList())
                        .build())
                .build();
    }

    @Override
    public boolean isFilter() {
        return false;
    }
}
