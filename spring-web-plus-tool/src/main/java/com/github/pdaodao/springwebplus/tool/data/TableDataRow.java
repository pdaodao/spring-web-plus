package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * 一行数据
 */
@Data
public class TableDataRow extends LinkedHashMap<String, Object> implements Serializable {

    public TableDataRow(int initialCapacity) {
        super(initialCapacity);
    }

    public TableDataRow() {
    }

    public Collection<Object> stringList(final String... fields){
        final List<Object> ret = new ArrayList<>(fields.length);
        if(ArrayUtil.isEmpty(fields)){
            for(final Object v: values()){
                ret.add(DataValueUtil.toString(v, null));
            }
            return ret;
        }
        for(final String f: fields){
            ret.add(getString(f, null));
        }
        return ret;
    }

    public static <T> TableDataRow from(final Map<String, T> map) {
        if (map == null) {
            return null;
        }
        final TableDataRow mapRow = new TableDataRow();
        mapRow.putAll(map);
        return mapRow;
    }

    public void mergeFrom(final TableDataRow row){
        if(row == null){
            return;
        }
        BeanUtils.copyPropertiesIgnoreNull(row, this);
    }

//    public TableDataRow toStringValue(){
//        final TableDataRow r = new TableDataRow();
//        for(Map.Entry<String, Object> entry: entrySet()){
//            r.put(entry.getKey(), DateTimeUti.formatIfDateElseToString(entry.getValue()));
//        }
//        return r;
//    }

    public static TableDataRow mapToCamelCase(final Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        final TableDataRow f = new TableDataRow();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            f.put(CharSequenceUtil.toCamelCase(entry.getKey()), entry.getValue());
        }
        return f;
    }

    /**
     * 转为驼峰
     *
     * @return
     */
    public TableDataRow toCamelCase() {
        final TableDataRow f = new TableDataRow();
        for (Map.Entry<String, Object> entry : entrySet()) {
            f.put(StrUtils.toCamelCase(entry.getKey()), entry.getValue());
        }
        return f;
    }

    public Set<String> keys() {
        return keySet();
    }

    public String getString(final String key, final String defaultValue) {
        final Object v = get(key);
        if(ObjectUtil.isNull(v)){
            return defaultValue;
        }
        return DataValueUtil.toString(v, null);
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

    public Date getDate(final String key, final Date defaultValue) {
        final Object v = get(key);
        if (ObjectUtil.isNull(v)) {
            return defaultValue;
        }
        return DataValueUtil.toDate(v);
    }

    public Boolean getBoolean(final String key, final Boolean defaultValue) {
        final Object v = get(key);
        if (ObjectUtil.isNull(v)) {
            return defaultValue;
        }
        return DataValueUtil.toBoolean(v);
    }
}

