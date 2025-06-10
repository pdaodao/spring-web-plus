package com.github.pdaodao.springwebplus.tool.util;

import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

public class JsonPathUtils {
    private static final JacksonJsonProvider JSON_PROVIDER = new JacksonJsonProvider();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JacksonMappingProvider MAPPING_PROVIDER = new JacksonMappingProvider(OBJECT_MAPPER);
    private static final Configuration DEFAULT_CONFIG =
            Configuration.builder().jsonProvider(JSON_PROVIDER).mappingProvider(MAPPING_PROVIDER).build();

    public static Object read(String json, String path) {
        return JsonPath.using(DEFAULT_CONFIG).parse(json).read(path);
    }

    public static <T> T read(String json, String path, Class<T> classType) {
        return JsonPath.using(DEFAULT_CONFIG).parse(json).read(path, classType);
    }

    public static <T> T read(String json, String path, TypeRef<T> typeRef) {
        return JsonPath.using(DEFAULT_CONFIG).parse(json).read(path, typeRef);
    }

    public static <T> List<T> readList(String json, String path, Class<T> classType) {
        return JsonPath.using(DEFAULT_CONFIG).parse(json).read(path, new TypeRef<List<T>>() {
            @Override
            public Type getType() {
                return OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, classType);
            }
        });
    }
}