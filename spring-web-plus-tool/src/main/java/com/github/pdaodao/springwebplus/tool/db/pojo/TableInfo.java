package com.github.pdaodao.springwebplus.tool.db.pojo;

import cn.hutool.db.meta.IndexInfo;
import cn.hutool.db.meta.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class TableInfo implements Serializable, Cloneable {
    /**
     * 列映射，列名-列对象
     */
    private final Map<String, ColumnInfo> columns = new LinkedHashMap<>();
    /**
     * table所在的schema
     */
    private String schema;
    /**
     * tables所在的catalog
     */
    private String catalog;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 注释
     */
    private String comment;
    /**
     * 主键字段名列表
     */
    private Set<String> pkNames = new LinkedHashSet<>();
    /**
     * 索引信息
     */
    private List<IndexInfo> indexInfoList;

    public TableInfo(String tableName) {
        this.setTableName(tableName);
    }

    public static TableInfo create(String tableName) {
        return new TableInfo(tableName);
    }

    /**
     * 给定列名是否为主键
     */
    public boolean isPk(String columnName) {
        return getPkNames().contains(columnName);
    }


    /**
     * 获取所有字段元信息
     *
     * @return 字段元信息集合
     * @since 4.5.8
     */
    public Collection<ColumnInfo> getColumns() {
        return this.columns.values();
    }

    public TableInfo setColumn(ColumnInfo column) {
        this.columns.put(column.getName(), column);
        if (column.isPk()) {
            addPk(column.getName());
        }
        return this;
    }

    /**
     * 添加主键
     *
     * @param pkColumnName 主键的列名
     * @return 自己
     */
    public TableInfo addPk(String pkColumnName) {
        this.pkNames.add(pkColumnName);
        return this;
    }

    @Override
    public Table clone() throws CloneNotSupportedException {
        return (Table) super.clone();
    }
}
