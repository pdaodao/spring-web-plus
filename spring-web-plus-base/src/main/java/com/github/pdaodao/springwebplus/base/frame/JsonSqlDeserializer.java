package com.github.pdaodao.springwebplus.base.frame;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.pdaodao.springwebplus.base.util.SqlEncodeUtil;
import java.io.IOException;

/**
 * sql json 反序列化
 */
public class JsonSqlDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return SqlEncodeUtil.decode(p.getText());
    }
}