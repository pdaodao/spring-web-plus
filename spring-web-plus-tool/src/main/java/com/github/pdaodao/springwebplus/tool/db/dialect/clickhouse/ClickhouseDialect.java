package com.github.pdaodao.springwebplus.tool.db.dialect.clickhouse;

import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDialect;

public class ClickhouseDialect extends MysqlDialect {
    @Override
    public DataTypeConverter dataTypeConverter() {
        return new ClickhouseDataTypeConverter();
    }

    @Override
    public DbDDLGen ddlGen() {
        return new ClickhouseDDLGen(this);
    }
}
