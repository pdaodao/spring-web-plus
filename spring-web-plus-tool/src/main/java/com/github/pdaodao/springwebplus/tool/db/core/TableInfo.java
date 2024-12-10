package com.github.pdaodao.springwebplus.tool.db.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private String dbSchema;
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
        if (CollUtil.isEmpty(columns)) {
            return ListUtil.empty();
        }
        return columns.stream().filter(t -> BooleanUtil.isTrue(t.isPk) || BooleanUtil.isTrue(t.isAuto))
                .map(t -> t.getName()).collect(Collectors.toList());
    }

    @Override
    protected TableInfo clone() {
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

    /**
     * 更新字段的title
     * @param name
     * @param title
     */
    public void updateTitle(final String name, final String title){
        if(StrUtil.isBlank(name) || CollUtil.isEmpty(columns)){
            return;
        }
        for(final TableColumn f: columns){
            if(StrUtil.equalsIgnoreCase(name, f.getName())){
                f.setTitle(title);
            }
        }
    }

    /**
     * 表名、字段转为下划线
     * @return
     */
    public TableInfo toUnderLine(){
        final TableInfo info = clone();
        info.setName(StrUtils.toUnderlineCase(info.getName()));
        if(CollUtil.isNotEmpty(info.getColumns())){
            info.getColumns().stream().forEach(t -> t.setName(StrUtils.toUnderlineCase(t.getName())));
        }
        if(CollUtil.isNotEmpty(info.getIndexList())){
            for(final TableIndex i: info.getIndexList()){
                if(CollUtil.isNotEmpty(i.getFields())){
                    final List<String> fs = i.getFields().stream().map(t -> StrUtils.toUnderlineCase(t)).collect(Collectors.toList());
                    i.setFields(fs);
                }
            }
        }
        return info;
    }

    /**
     * 字段转为驼峰
     * @return
     */
    public TableInfo toCamelCase(){
        final TableInfo info = clone();
        if(CollUtil.isNotEmpty(info.getColumns())){
            info.getColumns().stream().forEach(t -> t.setName(StrUtils.toCamelCase(t.getName())));
        }
        if(CollUtil.isNotEmpty(info.getIndexList())){
            for(final TableIndex i: info.getIndexList()){
                if(CollUtil.isNotEmpty(i.getFields())){
                   final List<String> fs =  i.getFields().stream().map(t -> StrUtils.toCamelCase(t)).collect(Collectors.toList());
                    i.setFields(fs);
                }
            }
        }
        return info;
    }

    /**
     * 根据字段名称 建立左右两边的字段对应关系
     * @param fromTable
     * @param toTable
     */
    public static void fieldMapping(final TableInfo fromTable, final TableInfo toTable){
        Preconditions.checkNotNull(fromTable, "来源表为空");
        Preconditions.checkNotNull(toTable, "目标表为空");
        if(CollUtil.isEmpty(fromTable.getColumns()) || CollUtil.isEmpty(toTable.getColumns())){
            return;
        }
        final List<String> fromUsed = new ArrayList<>();
        for(final TableColumn to: toTable.getColumns()){
            to.setFrom(null);
            for(final TableColumn f: fromTable.columns){
                if(StrUtil.equalsIgnoreCase(to.getName(), f.getName())
                        || StrUtil.equalsIgnoreCase(to.getTitle(), f.getTitle())
                        || StrUtils.equalsIgnoreUnderLine(to.getName(), f.getName())){
                    to.setFrom(f.getName());
                    fromUsed.add(f.getName());
                    break;
                }
            }
        }
    }
}
