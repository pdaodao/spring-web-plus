package com.github.pdaodao.springwebplus.tool.db.dialect.pg;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.DbType;
import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDbDialect;
import com.github.pdaodao.springwebplus.tool.db.util.DbUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

public class PgDialect extends BaseDbDialect {

    @Override
    public DbType dbType() {
        return DbType.Postgresql;
    }

    @Override
    public String driverName() {
        return "org.postgresql.Driver";
    }

    @Override
    public boolean isSupportSchema() {
        return true;
    }

    @Override
    public Integer fetchSize() {
        return 2000;
    }

    @Override
    public String buildUrl(DbInfo dbInfo) {
        Preconditions.checkNotBlank(dbInfo.getHost(), "主键地址不能为空");
        Preconditions.checkNotBlank(dbInfo.getDbName(), "库名不能为空");
        Preconditions.checkNotBlank(dbInfo.getDbSchema(), "请指定schema");
        if (dbInfo.getPort() == null) {
            dbInfo.setPort(5432);
        }
        final String fmt = "jdbc:postgresql://{}:{}/{}?currentSchema={}";
        final String url = StrUtil.format(fmt, dbInfo.getHost(), dbInfo.getPort(), dbInfo.getDbName(), dbInfo.getDbSchema());
        return url;
    }

    @Override
    public String keywordsFile() {
        return "/META-INF/db-keywords/postgresql.keywords";
    }

    @Override
    public String escape() {
        return "\"";
    }

    @Override
    public String pageSql(String sql, Integer offset, Integer size) {
        if (offset == null || offset < 1) {
            return DbUtil.pageSqlWrap(sql, "limit " + size);
        }
        return DbUtil.pageSqlWrap(sql, "limit " + size + " offset " + offset);
    }

    @Override
    public DataTypeConverter dataTypeConverter() {
        return new PgDataTypeConverter();
    }

    @Override
    public DbDDLGen ddlGen() {
        return new PgDDLGen(this);
    }
}
