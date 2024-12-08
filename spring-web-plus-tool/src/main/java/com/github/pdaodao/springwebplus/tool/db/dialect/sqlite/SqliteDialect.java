package com.github.pdaodao.springwebplus.tool.db.dialect.sqlite;

import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.DbType;
import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDbDialect;

public class SqliteDialect extends BaseDbDialect {
    @Override
    public DbType dbType() {
        return null;
    }

    @Override
    public String driverName() {
        return null;
    }

    @Override
    public String buildUrl(DbInfo dbInfo) {
        return null;
    }

    @Override
    public boolean isSupportSchema() {
        return false;
    }

    @Override
    public String escape() {
        return null;
    }

    @Override
    public String pageSql(String sql, Long offset, Long size) {
        return null;
    }

    @Override
    public DataTypeConverter dataTypeConverter() {
        return new SqliteDataTypeConverter();
    }

    @Override
    public DbDDLGen ddlGen() {
        return new SqliteDDLGen(this);
    }
}
