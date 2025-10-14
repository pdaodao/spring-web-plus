package com.github.pdaodao.springwebplus.base.frame;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.pdaodao.springwebplus.base.util.SqlEncodeUtil;
import java.io.IOException;

/**
 * sql 序列化
 */
public class JsonSqlSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(SqlEncodeUtil.encode(value));
    }
}