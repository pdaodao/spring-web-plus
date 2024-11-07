package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {
    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getStringJsonEscaped(String str) {
        //convert the hit data in a valid json string, pick the result json as it is.
        JsonStringEncoder e = JsonStringEncoder.getInstance();
        StringBuilder sb = new StringBuilder();
        e.quoteAsString(str, sb);
        return sb.toString();
    }

    public static List<String> toJsonStrings(final JsonNode jsonNode, final String key) throws JsonProcessingException {
        if (jsonNode == null || key == null) {
            return null;
        }
        if (!jsonNode.has(key)) {
            return null;
        }
        final JsonNode node = jsonNode.get(key);
        if (node instanceof NullNode) {
            return null;
        }
        final List<String> ret = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode arr : node) {
                final String t = toJsonString(arr, null);
                if (t != null) {
                    ret.add(t);
                }
            }
        } else {
            final String t = toJsonString(node);
            if (t != null) {
                ret.add(t);
            }
        }
        return ret;
    }

    public static String toJsonString(final JsonNode jsonNode, final String... key) throws JsonProcessingException {
        if (jsonNode == null || jsonNode instanceof NullNode) {
            return null;
        }
        JsonNode node = jsonNode;
        if (key != null) {
            for (final String k : key) {
                if (!node.has(k)) {
                    return null;
                }
                node = node.get(k);
            }
        }
        if (node == null) {
            return null;
        }
        if (node instanceof NullNode) {
            return null;
        }
        if (node instanceof TextNode) {
            return node.textValue();
        }
        return objectMapper.writeValueAsString(node);
    }

    public static Map<String, String> asMap(final Object obj) {
        if (obj == null) {
            return new LinkedHashMap<>();
        }
        return objectMapper.convertValue(obj, new TypeReference<LinkedHashMap<String, String>>() {
        });
    }

    public static Map<String, Object> strAsMap(final String json) throws JsonProcessingException {
        if (StrUtil.isBlank(json)) {
            return new LinkedHashMap<>();
        }
        return objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {
        });
    }

    public static Map<String, Object> asMapObject(final Object obj) {
        if (obj == null) {
            return new LinkedHashMap<>();
        }
        return objectMapper.convertValue(obj, new TypeReference<LinkedHashMap<String, Object>>() {
        });
    }

    /**
     * 字符串转为实体类对象,转换异常将被抛出
     *
     * @param content
     * @param valueType
     * @param <T>
     * @return
     */
    public <T> T toBean(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("json反序列化失败", e);
        }
    }

    /**
     * 字符串转为实体类对象，转换异常将被抛出
     */
    public <T> T toBean(String content, TypeReference<? extends T> ref) {
        try {
            return objectMapper.readValue(content, ref);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("json反序列化失败", e);
        }
    }

    /**
     * 字符串转为实体类对象列表，转换异常将被抛出
     *
     * @param content
     * @param valueType
     * @param <T>
     * @return
     */
    public <T> List<T> toList(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("json反序列化失败");
        }
    }
}
