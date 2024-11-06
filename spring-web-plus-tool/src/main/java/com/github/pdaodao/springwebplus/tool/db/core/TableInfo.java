package com.github.pdaodao.springwebplus.tool.db.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.BooleanUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据表结构信息
 */
@Data
public class TableInfo implements Serializable, Cloneable {
    /**
     * 备注
     */
    protected String remark;
    /**
     * 字段列表
     */
    protected List<TableColumn> columns;
    /**
     * 表名
     */
    private String name;
    /**
     * table所在的schema
     */
    private String schema;
    /**
     * 中文名称
     */
    private String title;
    private TableType tableType;
    /**
     * 索引信息
     */
    private List<TableIndex> indexList;

    public static TableInfo of(final String name) {
        final TableInfo info = new TableInfo();
        info.setName(name);
        return info;
    }

    public TableInfo addColumn(final TableColumn f) {
        if (f == null) {
            return this;
        }
        if (columns == null) {
            columns = new ArrayList<>();
        }
        columns.add(f);
        return this;
    }

    /**
     * 主键字段列表
     *
     * @return
     */
    public List<String> pkColumns() {
        if (CollUtil.isNotEmpty(columns)) {
            return ListUtil.empty();
        }
        return columns.stream().filter(t -> BooleanUtil.isTrue(t.isPk) || BooleanUtil.isTrue(t.isAuto))
                .map(t -> t.getName()).collect(Collectors.toList());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        final TableInfo t = new TableInfo();
        BeanUtil.copyProperties(this, t, "columns", "indexList");
        if (CollUtil.isNotEmpty(columns)) {
            final List<TableColumn> fs = columns.stream().map(f -> f.clone()).collect(Collectors.toList());
            t.setColumns(fs);
        }
        if (CollUtil.isNotEmpty(indexList)) {
            final List<TableIndex> fs = indexList.stream().map(f -> f.clone()).collect(Collectors.toList());
            t.setIndexList(fs);
        }
        return t;
    }
}
