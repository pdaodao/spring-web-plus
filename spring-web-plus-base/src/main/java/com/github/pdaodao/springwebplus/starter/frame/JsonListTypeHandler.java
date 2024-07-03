package com.github.pdaodao.springwebplus.starter.frame;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 数组数据 json 处理
 * @param <T>
 */
public abstract class JsonListTypeHandler<T extends Object> extends AbstractJsonTypeHandler<List<T>> {
    private static ObjectMapper OBJECT_MAPPER;
    JavaType javaType;

    public JsonListTypeHandler(Class<T> clazz) {
        Assert.notNull(clazz, "JsonTypeHandler class is null");
        ResolvableType resolvableType = ResolvableType.forClass(getClass());
        Type type = resolvableType.as(JsonListTypeHandler.class).getGeneric().getType();
        javaType = getObjectMapper()
                .getTypeFactory()
                .constructParametricType(ArrayList.class, getObjectMapper().constructType(type));
    }

    @Override
    protected List<T> parse(final String json) {
        if (StringUtils.isEmpty(json)) return null;
        try {
            return (List<T>) getObjectMapper().readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    protected String toJson(List<T> obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectMapper getObjectMapper() {
        if (null == OBJECT_MAPPER) {
            OBJECT_MAPPER = new ObjectMapper();
        }
        return OBJECT_MAPPER;
    }
}