package com.github.pdaodao.springwebplus.tool.db.dialect.gbase;

import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDialect;

public class GbaseDialect extends MysqlDialect {
    @Override
    public DataTypeConverter dataTypeConverter() {
        return new GbaseDataTypeConverter();
    }

    @Override
    public DbDDLGen ddlGen() {
        return new GbaseDDLGen(this);
    }
}
