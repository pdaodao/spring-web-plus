package com.github.pdaodao.springwebplus.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pdaodao.springwebplus.base.frame.JsonSqlDeserializer;
import com.github.pdaodao.springwebplus.base.frame.JsonSqlSerializer;

public interface WithSql {
    @JsonSerialize(using = JsonSqlSerializer.class)
    String getSqlText();

    @JsonDeserialize(using = JsonSqlDeserializer.class)
    void setSqlText(String sql);
}