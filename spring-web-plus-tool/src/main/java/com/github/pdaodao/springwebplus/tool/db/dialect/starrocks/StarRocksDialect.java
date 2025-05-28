package com.github.pdaodao.springwebplus.tool.db.dialect.starrocks;

import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDialect;

public class StarRocksDialect extends MysqlDialect {

    @Override
    public DataTypeConverter dataTypeConverter() {
        return new StarRocksDataTypeConverter();
    }

    @Override
    public DbDDLGen ddlGen() {
        return new StarRocksDDLGen(this);
    }
}
