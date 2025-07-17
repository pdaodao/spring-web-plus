package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

/**
 * 小于 <
 */
public final class LessThanDsl extends EsDslBuilder {

    public static final LessThanDsl instance = new LessThanDsl();

    private LessThanDsl() {
    }


    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        checkOnlyOneArg("<", args);
        final NumberRangeQuery rr = new NumberRangeQuery.Builder()
                .field(field)
                .lt(NumberUtil.parseDouble(ObjectUtil.toString(args.get(0))))
                .build();
        return new Query.Builder().range(rr._toRangeQuery()).build();
    }
}