package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * fielda in (a, b, c)
 */
public class InDsl extends EsDslBuilder {

    public static final InDsl instance = new InDsl();

    private InDsl() {
    }

    @Override
    public boolean isFilter() {
        return true;
    }

    @Override
    public Query doBuild(String field, DslConfig dslConfig, String method, List<Object> args) {
        if (args == null) return null;
        if (args.size() == 1) {
            final TermQuery termQuery = new TermQuery.Builder()
                    .field(field)
                    .value(FieldValue.of(args.get(0)))
                    .build();
            return new Query.Builder().term(termQuery).build();
        }
        final List<FieldValue> values = new ArrayList<>();
        for(final Object obj: args){
            if(obj == null){
                continue;
            }
            values.add(FieldValue.of(obj));
        }
        final TermsQuery termsQuery = new TermsQuery.Builder()
                .field(field)
                .terms(new TermsQueryField.Builder().value(values).build())
                .build();
        return termsQuery._toQuery();
    }
}
