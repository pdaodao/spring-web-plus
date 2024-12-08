package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Data
public class StreamRow implements Serializable, Cloneable {
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
    private TableDataRow data;

    /**
     * cdc before 你懂的
     */
    private TableDataRow before;

    public StreamRow() {
    }

    public StreamRow(RowKind kind, TableDataRow map) {
        this.kind = kind;
        this.data = map;
    }

    public static StreamRow ofKind(final RowKind kind) {
        return new StreamRow(kind, new TableDataRow());
    }

    public static StreamRow of(final int size) {
        return new StreamRow(RowKind.INSERT, new TableDataRow(size));
    }


    public static StreamRow ofKind(final RowKind kind, final Map<String, Object> map) {
        return new StreamRow(kind, TableDataRow.from(map));
    }

    public static StreamRow end() {
        final StreamRow row = new StreamRow();
        row.setKind(RowKind.END);
        return row;
    }

    public RowKind getKind() {
        return kind;
    }

    public StreamRow setKind(RowKind kind) {
        this.kind = kind;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public StreamRow setTag(String tag) {
        this.tag = tag;
        return this;
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
            data = new TableDataRow();
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
    protected StreamRow clone() throws CloneNotSupportedException {
        final StreamRow row = ofKind(kind, data);
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
}
