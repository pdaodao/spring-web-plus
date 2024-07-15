package com.github.pdaodao.springwebplus.tool.db.dialect.sqlite;

import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDbDialect;

public class SqliteDialect extends BaseDbDialect {
    @Override
    public DataTypeConverter dataTypeConverter() {
        return new SqliteDataTypeConverter();
    }

    @Override
    public DbDDLGen ddlGen() {
        return new SqliteDDLGen(this);
    }
}
