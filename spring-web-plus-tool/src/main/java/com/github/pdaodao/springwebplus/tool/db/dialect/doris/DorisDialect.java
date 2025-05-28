package com.github.pdaodao.springwebplus.tool.db.dialect.doris;

import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDialect;

public class DorisDialect extends MysqlDialect {
    @Override
    public DataTypeConverter dataTypeConverter() {
        return new DorisDataTypeConverter();
    }

    @Override
    public DbDDLGen ddlGen() {
        return new DorisDDLGen(this);
    }
}
