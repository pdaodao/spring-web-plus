package com.github.pdaodao.springwebplus.tool.nosql.parser;

public enum JOperator {

    GreaterThan(">"),

    GreaterThanOrEqual(">="),

    LessThan("<"),

    LessThanOrEqual("<="),

    BETWEEN("BETWEEN"),

    NOTBETWEEN("NOT BETWEEN"),

    IN("IN"),

    Like("LIKE"),

    NotLike("NOT LIKE"),

    IsNull("IS"),

    IsNotNull("IS NOT"),

    Match("MATCH"),

    MatchPhrase("MATCH_PHRASE"),

    MatchPhrasePrefix("MATCH_PHRASE_PREFIX"),

    CommonsTerms("COMMON_TERMS"),

    QueryString("QUERY_STRING"),

    NotEqual("!="),

    Equality("="),

    BooleanAnd("AND"),

    BooleanOr("OR");

    // 操作符名称
    public final String name;

    private JOperator(String name) {
        this.name = name;
    }

    public static JOperator from(String name) {
        for (JOperator operator : values()) {
            if (operator.name.equalsIgnoreCase(name)) {
                return operator;
            }
        }
        return null;
    }

    public boolean isLogical() {
        return this == BooleanAnd || this == BooleanOr;
    }

}
