package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

public class IsNullDsl extends EsDslBuilder {

    public static final IsNullDsl instance = new IsNullDsl();

    private IsNullDsl() {
    }

    @Override
    public boolean isFilter() {
        return true;
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        final BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        boolQuery.mustNot(new ExistsQuery.Builder().field(field).build()._toQuery());
        return boolQuery.build()._toQuery();
    }
}
