package com.github.pdaodao.springwebplus.tool.db.dialect.dm;

import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDialect;

public class DmDialect extends MysqlDialect {
    @Override
    public DataTypeConverter dataTypeConverter() {
        return new DmDataTypeConverter();
    }

    @Override
    public DbDDLGen ddlGen() {
        return new DmDDLGen(this);
    }
}
