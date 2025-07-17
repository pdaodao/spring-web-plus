package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;

import java.util.List;

/**
 * match(key, 'f1, 'f2')
 */
public class MatchDsl extends EsDslBuilder {

    public static final MatchDsl instance = new MatchDsl();

    public MatchDsl() {
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        checkMoreThanOneArg("match", args);
        Object key = args.get(0);
        return matchQuery(key, dslConfig, getFieldsFromArgs(args, dslConfig));
    }

    @Override
    public boolean isFilter() {
        return false;
    }
}
