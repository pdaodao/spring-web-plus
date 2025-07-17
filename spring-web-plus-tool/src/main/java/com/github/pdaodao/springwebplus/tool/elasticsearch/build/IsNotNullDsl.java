package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

public class IsNotNullDsl extends EsDslBuilder {

    public static final IsNotNullDsl instance = new IsNotNullDsl();

    private IsNotNullDsl() {
    }

    @Override
    public boolean isFilter() {
        return true;
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        return new Query.Builder().exists(new ExistsQuery.Builder().field(field).build()).build();
    }
}
