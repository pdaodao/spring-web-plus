package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

/**
 * fielda like `abc%`
 */
public class LikeDsl extends EsDslBuilder {
    public static final LikeDsl instance = new LikeDsl();

    public LikeDsl() {
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        checkOnlyOneArg("like", args);

        return matchQuery(args.get(0), dslConfig, field);
    }

    @Override
    public boolean isFilter() {
        return false;
    }
}
