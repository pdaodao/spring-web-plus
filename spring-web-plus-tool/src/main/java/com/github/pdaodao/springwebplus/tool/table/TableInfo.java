package com.github.pdaodao.springwebplus.tool.table;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据表结构信息
 */
@Data
public class TableInfo implements Serializable, Cloneable {
    /**
     * 表名
     */
    private String name;

    /**
     * 备注
     */
    protected String remark;

    /**
     * 中文名称
     */
    private String title;

    /**
     * 字段列表
     */
    protected List<TableField> fields;

    /**
     * table所在的schema
     */
    private String dbSchema;


    private TableType tableType;

    // 数据表行数
    protected Long dataRows;

    /**
     * 索引信息
     */
    private List<TableIndex> indexList;

    /**
     * 分区
     */
    private TablePartition tablePartition;

    /**
     * 副本数 >= 1
     */
    private Integer replicationNum;

    /**
     * 分桶数 >= 1
     */
    private Integer bucketNum;

    public static TableInfo of(final String name) {
        final TableInfo info = new TableInfo();
        info.setName(name);
        return info;
    }

    /**
     * 根据字段名称 建立左右两边的字段对应关系
     *
     * @param fromTable
     * @param toTable
     */
    public static void fieldMapping(final TableInfo fromTable, final TableInfo toTable) {
        Preconditions.checkNotNull(fromTable, "来源表为空");
        Preconditions.checkNotNull(toTable, "目标表为空");
        if (CollUtil.isEmpty(fromTable.getFields()) || CollUtil.isEmpty(toTable.getFields())) {
            return;
        }
        final List<String> fromUsed = new ArrayList<>();
        for (final TableField to : toTable.getFields()) {
            to.setFrom(null);
            for (final TableField f : fromTable.fields) {
                if (BooleanUtil.isTrue(to.getIsAuto()) && StrUtil.equals(f.getTitle(), "序号")) {
                    to.setFrom(f.getName());
                    fromUsed.add(f.getName());
                    break;
                }
                if (StrUtil.equalsIgnoreCase(to.getName(), f.getName())
                        || StrUtil.equalsIgnoreCase(to.getName(), f.getTitle())
                        || StrUtil.equalsIgnoreCase(to.getTitle(), f.getTitle())
                        || StrUtil.equalsIgnoreCase(to.getTitle(), f.getName())
                        || StrUtils.equalsIgnoreUnderLine(to.getName(), f.getName())) {
                    to.setFrom(f.getName());
                    fromUsed.add(f.getName());
                    break;
                }
            }
        }
    }

    public TableInfo addColumn(final TableField f) {
        if (f == null) {
            return this;
        }
        if (fields == null) {
            fields = new ArrayList<>();
        }
        fields.add(f);
        return this;
    }

    /**
     * 主键字段列表
     * @return
     */
    public List<TableField> pkColumns() {
        if (CollUtil.isEmpty(fields)) {
            return ListUtil.empty();
        }
        final List<TableField> pkFields = new ArrayList<>();
        for(final TableField f: fields){
            if(BooleanUtil.isTrue(f.getIsAuto())){
                return ListUtil.of(f);
            }
            if(BooleanUtil.isTrue(f.getIsPk())){
                pkFields.add(f);
            }
        }
        return pkFields;
    }

    /**
     * 主键字段名称
     * @return
     */
    public List<String> pkColumnNames() {
        return pkColumns().stream().map(t -> t.getName()).collect(Collectors.toList());
    }

    @Override
    public TableInfo clone() {
        final TableInfo t = new TableInfo();
        BeanUtil.copyProperties(this, t, "columns", "indexList");
        if (CollUtil.isNotEmpty(fields)) {
            final List<TableField> fs = fields.stream().map(f -> f.clone()).collect(Collectors.toList());
            t.setFields(fs);
        }
        if (CollUtil.isNotEmpty(indexList)) {
            final List<TableIndex> fs = indexList.stream().map(f -> f.clone()).collect(Collectors.toList());
            t.setIndexList(fs);
        }
        return t;
    }

    /**
     * 更新字段的title
     *
     * @param name
     * @param title
     */
    public void updateTitle(final String name, final String title) {
        if (StrUtil.isBlank(name) || CollUtil.isEmpty(fields)) {
            return;
        }
        for (final TableField f : fields) {
            if (StrUtil.equalsIgnoreCase(name, f.getName())) {
                f.setTitle(title);
            }
        }
    }

    /**
     * 表名、字段转为下划线
     *
     * @return
     */
    public TableInfo toUnderLine() {
        final TableInfo info = clone();
        info.setName(StrUtils.toUnderlineCase(info.getName()));
        if (CollUtil.isNotEmpty(info.getFields())) {
            info.getFields().stream().forEach(t -> t.setName(StrUtils.toUnderlineCase(t.getName())));
        }
        if (CollUtil.isNotEmpty(info.getIndexList())) {
            for (final TableIndex i : info.getIndexList()) {
                if (CollUtil.isNotEmpty(i.getFields())) {
                    final List<String> fs = i.getFields().stream().map(t -> StrUtils.toUnderlineCase(t)).collect(Collectors.toList());
                    i.setFields(fs);
                }
            }
        }
        return info;
    }

    /**
     * 字段转为驼峰
     *
     * @return
     */
    public TableInfo toCamelCase() {
        final TableInfo info = clone();
        if (CollUtil.isNotEmpty(info.getFields())) {
            info.getFields().stream().forEach(t -> t.setName(StrUtils.toCamelCase(t.getName())));
        }
        if (CollUtil.isNotEmpty(info.getIndexList())) {
            for (final TableIndex i : info.getIndexList()) {
                if (CollUtil.isNotEmpty(i.getFields())) {
                    final List<String> fs = i.getFields().stream().map(t -> StrUtils.toCamelCase(t)).collect(Collectors.toList());
                    i.setFields(fs);
                }
            }
        }
        return info;
    }

    public Map<String, TableField> fieldMap(){
        final Map<String, TableField> map = new LinkedCaseInsensitiveMap<>();
        if(CollUtil.isEmpty(fields)){
            return map;
        }
        for(final TableField f: fields){
            map.put(f.getName(), f);
        }
        return map;
    }

    public TableField autoIdColumn(){
        if(CollUtil.isEmpty(fields)){
            return null;
        }
        for(final TableField f: fields){
            if(BooleanUtil.isTrue(f.getIsAuto())){
                return f;
            }
        }
        return null;
    }

}
