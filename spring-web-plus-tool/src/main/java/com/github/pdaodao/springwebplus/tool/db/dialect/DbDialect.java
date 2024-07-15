package com.github.pdaodao.springwebplus.tool.db.dialect;

/**
 * 数据库方言
 */
public interface DbDialect {
    /**
     * 类型转换器
     *
     * @return
     */
    DataTypeConverter dataTypeConverter();

    /**
     * ddl 生成器
     *
     * @return
     */
    DbDDLGen ddlGen();
}
