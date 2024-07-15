package com.github.pdaodao.springwebplus.tool.db.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段 DDL 定义 类型 和 默认值
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldTypeName {
    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 默认值
     */
    private String columnDef;


    public static FieldTypeName of(final String type, String df) {
        return new FieldTypeName(type, df);
    }
}
