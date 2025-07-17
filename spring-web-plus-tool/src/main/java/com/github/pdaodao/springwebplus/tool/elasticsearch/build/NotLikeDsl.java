package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

/**
 * fielda NOT LIKE `ABC`
 */
public class NotLikeDsl extends EsDslBuilder {
    public static final NotLikeDsl instance = new NotLikeDsl();

    private NotLikeDsl() {
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        checkOnlyOneArg("not like", args);
        final BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        boolQuery.mustNot(matchQuery(args.get(0), dslConfig, field));
        return boolQuery.build()._toQuery();
    }
}
