package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

/**
 * 相等  f1 = 123
 */
public class EqualityDsl extends EsDslBuilder {
    public static final EqualityDsl instance = new EqualityDsl();

    private EqualityDsl() {
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        checkOnlyOneArg("=", args);
        Object obj = args.get(0);
        if (obj == null) return null;
        if (obj instanceof String) {
            if (EmptyStringShow.equalsIgnoreCase(obj.toString())) {
                obj = StrUtil.EMPTY;
            }
            if (obj.toString().equalsIgnoreCase("true")) {
                obj = true;
            } else if (obj.toString().equalsIgnoreCase("false")) {
                obj = false;
            }
        }
        final TermQuery termQuery = new TermQuery.Builder()
                .field(field)
                .value(FieldValue.of(obj))
                .build();
        return new Query.Builder().term(termQuery).build();
    }
}
