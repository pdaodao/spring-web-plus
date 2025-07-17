package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import cn.hutool.core.util.ObjectUtil;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

/**
 * query_string(stringtext)
 */
public class QueryStringDsl extends EsDslBuilder {
    public static final QueryStringDsl instance = new QueryStringDsl();

    private QueryStringDsl() {
    }

    @Override
    protected boolean isNeedEscape() {
        return false;
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        checkOnlyOneArg("query_string", args);

        final QueryStringQuery termQuery = new QueryStringQuery.Builder()
                .query(ObjectUtil.toString(args.get(0)))
                .build();
        return new Query.Builder().queryString(termQuery).build();
    }
}
