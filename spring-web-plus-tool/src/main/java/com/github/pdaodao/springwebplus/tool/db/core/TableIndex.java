package com.github.pdaodao.springwebplus.tool.db.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据表索引
 */
@Data
public class TableIndex implements Serializable, Cloneable {
    /**
     * 索引名称(英文)
     */
    private String name;

    /**
     * 中文名称
     */
    private String title;

    /**
     * 备注
     */
    private String remark;

    /**
     * 字段列表
     */
    private List<String> fields;

    /**
     * 是否唯一索引
     */
    private Boolean isUnique;

    public static TableIndex of(final String name) {
        final TableIndex tableIndex = new TableIndex();
        tableIndex.setName(name);
        return tableIndex;
    }

    public TableIndex addColumn(final String columnName) {
        if (StrUtil.isBlank(columnName)) {
            return this;
        }
        if (fields == null) {
            fields = new ArrayList<>();
        }
        fields.add(columnName.trim());
        return this;
    }


    @Override
    protected TableIndex clone() {
        final TableIndex t = new TableIndex();
        BeanUtil.copyProperties(this, t);
        return t;
    }
}
