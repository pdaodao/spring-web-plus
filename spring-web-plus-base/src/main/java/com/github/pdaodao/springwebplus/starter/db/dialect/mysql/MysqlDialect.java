package com.github.pdaodao.springwebplus.starter.db.dialect.mysql;

import com.github.pdaodao.springwebplus.starter.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.starter.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.starter.db.dialect.base.BaseDbDialect;

public class MysqlDialect extends BaseDbDialect {
    @Override
    public DataTypeConverter dataTypeConverter() {
        return new MysqlDataTypeConverter();
    }

    @Override
    public DbDDLGen ddlGen() {
        return new MysqlDDLGen(this);
    }
}
