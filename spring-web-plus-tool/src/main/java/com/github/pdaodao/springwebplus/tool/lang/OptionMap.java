package com.github.pdaodao.springwebplus.tool.lang;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import java.io.Serializable;
import java.util.Map;

public class OptionMap extends LinkedCaseInsensitiveMap<Object> implements Serializable {
    public OptionMap option(final String key, final Object value) {
        Preconditions.checkNotBlank(key, "option key should not empty.");
        put(key, value);
        return this;
    }

    public String getString(final String key, final String defaultValue) {
        final Object v = get(key);
        if(ObjectUtil.isNull(v)){
            return null;
        }
        if(v instanceof String){
            return StrUtil.trim((String) v);
        }
        return StrUtil.trim(StrUtil.toStringOrNull(v));
    }

    public Integer getInt(final String key, final Integer defaultValue) {
        final Object v = get(key);
        if (ObjectUtil.isNull(v)) {
            return defaultValue;
        }
        return DataValueUtil.toInt(v);
    }

    public Long getLong(final String key, final Long defaultValue) {
        final Object v = get(key);
        if (ObjectUtil.isNull(v)) {
            return defaultValue;
        }
        return DataValueUtil.toLong(v);
    }

    public Double getDouble(final String key, final Double defaultValue) {
        final Object v = get(key);
        if (ObjectUtil.isNull(v)) {
            return defaultValue;
        }
        return DataValueUtil.toDouble(v);
    }


    public Boolean getBoolean(final String key, final Boolean defaultValue) {
        final Object v = get(key);
        if(ObjectUtil.isNull(v)){
            return defaultValue;
        }
        return DataValueUtil.toBoolean(v);
    }

    public java.util.Properties toProperties() {
        final java.util.Properties p = new java.util.Properties();
        for (Map.Entry<String, Object> entry : entrySet()) {
            p.put(entry.getKey(), entry.getValue());
        }
        return p;
    }
}
