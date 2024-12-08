package com.github.pdaodao.springwebplus.tool.lang;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * 配置项
 */
public class ConfigOptions extends LinkedCaseInsensitiveMap<String> implements Serializable {

    public ConfigOptions option(final String key, final Boolean value) {
        Preconditions.checkNotBlank(key, "connect option key should not empty.");
        put(key, value == null ? null : value.toString());
        return this;
    }

    public ConfigOptions option(final String key, final Object value) {
        Preconditions.checkNotBlank(key, "connect option key should not empty.");
        put(key, value == null ? null : StrUtil.toStringOrNull(value));
        return this;
    }


    public ConfigOptions option(final String key, final Integer value) {
        Preconditions.checkNotBlank(key, "connect option key should not empty.");
        put(key, value == null ? null : value.toString());
        return this;
    }

    public ConfigOptions option(final String key, final Long value) {
        Preconditions.checkNotBlank(key, "connect option key should not empty.");
        put(key, value == null ? null : value.toString());
        return this;
    }

    public ConfigOptions option(final String key, final String value) {
        Preconditions.checkNotBlank(key, "connect option key should not empty.");
        put(key, value);
        return this;
    }

    public String getString(final String key, final String defaultValue) {
        return getOrDefault(key, defaultValue);
    }

    public char getChar(final String key, final char defaultValue) {
        final String f = get(key);
        if (f == null) {
            return defaultValue;
        }
        if (f.length() == 0) {
            return '\u0000';
        }
        if (f.length() == 1) {
            return f.charAt(0);
        }
        throw new IllegalArgumentException("param " + f + " exceed one char error.");
    }

    public Integer getInt(final String key, final Integer defaultValue) {
        final String v = get(key);
        if (StrUtil.isBlank(v)) {
            return defaultValue;
        }
        return Integer.parseInt(v);
    }

    public Boolean getBoolean(final String key, final Boolean defaultValue) {
        final String v = get(key);
        if (StrUtil.isBlank(v)) {
            return defaultValue;
        }
        if ("true".equalsIgnoreCase(v)) {
            return true;
        }
        if ("false".equals(v)) {
            return false;
        }
        return defaultValue;
    }

    public Properties toProperties() {
        final Properties p = new Properties();
        for (Map.Entry<String, String> entry : entrySet()) {
            p.put(entry.getKey(), entry.getValue());
        }
        return p;
    }
}