package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

/**
 * 大于等于 >=
 */
public class GreaterThanOrEqualDsl extends EsDslBuilder {

    public static final GreaterThanOrEqualDsl instance = new GreaterThanOrEqualDsl();

    private GreaterThanOrEqualDsl() {
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        checkOnlyOneArg(">=", args);
        final NumberRangeQuery rr = new NumberRangeQuery.Builder()
                .field(field)
                .gte(NumberUtil.parseDouble(ObjectUtil.toString(args.get(0))))
                .build();
        return new Query.Builder().range(rr._toRangeQuery()).build();
    }
}
