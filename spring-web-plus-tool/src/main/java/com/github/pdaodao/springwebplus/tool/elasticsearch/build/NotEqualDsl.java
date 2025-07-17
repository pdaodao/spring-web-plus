package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

/**
 * 不等于 f1 != 1
 */
public class NotEqualDsl extends EsDslBuilder {

    public static final NotEqualDsl instance = new NotEqualDsl();

    private NotEqualDsl() {
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        checkOnlyOneArg("!=", args);
        final BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        final TermQuery termQuery = new TermQuery.Builder()
                    .field(field)
                    .value(FieldValue.of(args.get(0)))
                    .build();
        boolQuery.mustNot(new Query.Builder().term(termQuery).build());
        return boolQuery.build()._toQuery();
    }
}
