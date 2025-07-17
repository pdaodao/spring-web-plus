package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;

/**
 * common_terms(text, 'f1','f2'...)
 */
public class CommonsTermsDsl extends EsDslBuilder {

    public static final CommonsTermsDsl instance = new CommonsTermsDsl();

    private CommonsTermsDsl() {
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        checkMoreThanOneArg("common_terms", args);
        String[] fields = getFieldsFromArgs(args, dslConfig);
        if (fields.length == 1) {
            final TermQuery termQuery = new TermQuery.Builder()
                    .field(fields[0])
                    .value(fields[1])
                    .build();
            return new Query.Builder().term(termQuery).build();
        }
        final BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        for (String f : fields) {
            final TermQuery termQuery = new TermQuery.Builder()
                    .field(f)
                    .value(FieldValue.of(args.get(0)))
                    .build();
            boolQuery.should(new Query.Builder().term(termQuery).build());
        }
        return boolQuery.build()._toQuery();
    }

    @Override
    public boolean isFilter() {
        return false;
    }
}
