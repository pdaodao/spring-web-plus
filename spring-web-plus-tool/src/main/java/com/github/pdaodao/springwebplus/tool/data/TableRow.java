package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.*;

@Data
public class TableRow implements Serializable, Cloneable {
    /**
     * 行数据类型
     */
    private RowKind kind;

    /**
     * 数据标签：如表名，topic名称等
     */
    private String tag;

    /**
     * 以 map 表达的行数据
     */
    private TableRowData data;

    /**
     * cdc before 你懂的
     */
    private TableRowData before;

    public TableRow() {
    }

    public TableRow(RowKind kind, TableRowData map) {
        this.kind = kind;
        this.data = map;
    }

    public static TableRow ofKind(final RowKind kind) {
        return new TableRow(kind, new TableRowData());
    }

    public static TableRow of(final int size) {
        return new TableRow(RowKind.INSERT, new TableRowData(size));
    }


    public static TableRow ofKind(final RowKind kind, final Map<String, Object> map) {
        return new TableRow(kind, TableRowData.from(map));
    }

    public static TableRow end() {
        final TableRow row = new TableRow();
        row.setKind(RowKind.END);
        return row;
    }

    public RowKind getKind() {
        return kind;
    }

    public TableRow setKind(RowKind kind) {
        this.kind = kind;
        return this;
    }

    public String pkValueString(final Set<String> pks){
        if(CollUtil.isEmpty(pks)){
            return null;
        }
        final TableRowData r = before != null ? before : data;
        final StringBuilder sb = new StringBuilder();
        for(final String t: pks){
            if(!sb.isEmpty()){
                sb.append("-");
            }
            sb.append(r.getString(t, null));
        }
        return sb.toString();
    }

    public String getTag() {
        return tag;
    }

    public TableRow setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public Object get(final String name) {
        if (data != null) {
            return data.get(name);
        }
        return null;
    }

    public Object getField(final String name) {
        if (data != null) {
            return data.get(name);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getFieldAs(String name) {
        return (T) getField(name);
    }

    public void setField(String name, Object value) {
        if (data == null) {
            data = new TableRowData();
        }
        data.put(name, value);
    }

    public void clear() {
        if (data != null) {
            data.clear();
        }
    }

    @Override
    public String toString() {
        if (before == null) {
            return JSONUtil.toJsonStr(data);
        }
        return JSONUtil.toJsonStr(this);
    }

    @Override
    protected TableRow clone() throws CloneNotSupportedException {
        final TableRow row = ofKind(kind, data);
        row.setTag(tag);
        row.setBefore(before);
        return row;
    }

    public boolean isEnd() {
        return RowKind.END == kind;
    }

    public Collection<Object> values() {
        if (data == null) {
            return ListUtil.empty();
        }
        return data.values();
    }

    public Set<String> keys() {
        if (data == null) {
            return new LinkedHashSet<>();
        }
        return data.keySet();
    }

    public String getString(final String key, final String defaultValue) {
        return data.getString(key, defaultValue);
    }

    public Integer getInt(final String key, final Integer defaultValue) {
        return data.getInt(key, defaultValue);
    }

    public Long getLong(final String key, final Long defaultValue) {
        return data.getLong(key, defaultValue);
    }

    public Double getDouble(final String key, final Double defaultValue) {
        return data.getDouble(key, defaultValue);
    }

    public Date getDate(final String key, final Date defaultValue) {
        return data.getDate(key, defaultValue);
    }

    public Boolean getBoolean(final String key, final Boolean defaultValue) {
        return data.getBoolean(key, defaultValue);
    }
}
