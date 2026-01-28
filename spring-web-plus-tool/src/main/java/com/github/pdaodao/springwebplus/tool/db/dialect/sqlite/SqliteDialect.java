package com.github.pdaodao.springwebplus.tool.db.dialect.sqlite;

import com.github.pdaodao.springwebplus.tool.db.DbUtil;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import com.github.pdaodao.springwebplus.tool.table.DbType;
import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDbDialect;

public class SqliteDialect extends BaseDbDialect {
    @Override
    public DbType dbType() {
        return DbType.Sqlite;
    }

    @Override
    public String driverName() {
        return "org.sqlite.JDBC";
    }

    @Override
    public String keywordsFile() {
        return "/META-INF/db-keywords/sqlite.keywords";
    }

    @Override
    public String buildUrl(DbInfo dbInfo) {
        // spring.datasource.url=jdbc:sqlite:db.db
        return null;
    }

    @Override
    protected String buildUrlDriverName() {
        return "sqlite";
    }

    @Override
    public boolean isSupportSchema() {
        return false;
    }

    @Override
    public String escape() {
        return "\"";
    }

    @Override
    public String pageSql(String sql, Long offset, Long size) {
        if (offset == null || offset < 1) {
            return DbUtil.pageSqlWrap(sql, "limit " + size);
        }
        return DbUtil.pageSqlWrap(sql, "limit " + size + " offset " + offset);
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
