package com.github.pdaodao.springwebplus.tool.db.dialect.mysql;

import com.github.pdaodao.springwebplus.tool.table.DbType;
import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFunction;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDbDialect;
import com.github.pdaodao.springwebplus.tool.db.DbUtil;

public class MysqlDialect extends BaseDbDialect {
    @Override
    public DbType dbType() {
        return DbType.Mysql;
    }

    @Override
    public String driverName() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public DbFunction dbFunction() {
        return new MySqlDbFunction();
    }

    @Override
    public boolean isSupportSchema() {
        return false;
    }

    @Override
    public Integer fetchSize() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected String buildUrlDriverName() {
        return "mysql";
    }

    @Override
    protected String buildUrlDefaultProperties() {
        return "useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&useInformationSchema=true&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true&useServerPrepStmts=true&useCompression=true";
    }

    @Override
    public String keywordsFile() {
        return "/META-INF/db-keywords/mysql.keywords";
    }

    @Override
    public String escape() {
        return "`";
    }

    @Override
    public String pageSql(String sql, Long offset, Long size) {
        if (offset == null || offset < 1) {
            return DbUtil.pageSqlWrap(sql, "limit " + size);
        }
        return DbUtil.pageSqlWrap(sql, "limit " + offset + " , " + size);
    }

    @Override
    public DataTypeConverter dataTypeConverter() {
        return new MysqlDataTypeConverter();
    }

    @Override
    public DbDDLGen ddlGen() {
        return new MysqlDDLGen(this);
    }
}
