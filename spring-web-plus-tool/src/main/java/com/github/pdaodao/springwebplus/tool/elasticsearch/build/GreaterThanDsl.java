package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

/**
 * 大于 >
 */
public class GreaterThanDsl extends EsDslBuilder {

    public static final GreaterThanDsl instance = new GreaterThanDsl();

    private GreaterThanDsl() {
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args){
        checkOnlyOneArg(">", args);
        final NumberRangeQuery rr = new NumberRangeQuery.Builder()
                .field(field)
                .gt(NumberUtil.parseDouble(ObjectUtil.toString(args.get(0))))
                .build();
        return new Query.Builder().range(rr._toRangeQuery()).build();
    }
}
