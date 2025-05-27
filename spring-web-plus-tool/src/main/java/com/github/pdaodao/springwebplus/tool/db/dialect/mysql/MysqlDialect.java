package com.github.pdaodao.springwebplus.tool.db.dialect.mysql;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.DbType;
import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDDLGen;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFunction;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDbDialect;
import com.github.pdaodao.springwebplus.tool.db.util.DbUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

public class MysqlDialect extends BaseDbDialect {
    public static final String UrlSuffix = "useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true&useServerPrepStmts=true&useCompression=true";
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
    public String buildUrl(DbInfo dbInfo) {
        Preconditions.checkNotBlank(dbInfo.getHost(), "主键地址不能为空");
        Preconditions.checkNotBlank(dbInfo.getDbName(), "库名不能为空");
        if (dbInfo.getPort() == null) {
            dbInfo.setPort(3306);
        }
        if(StrUtil.isBlank(dbInfo.getUrl())){
            final String fmt = "jdbc:mysql://{}:{}/{}?"+UrlSuffix;
            final String url = StrUtil.format(fmt, dbInfo.getHost(), dbInfo.getPort(), dbInfo.getDbName());
            dbInfo.setUrl(url);
            return url;
        }
        if (!dbInfo.getUrl().contains("rewriteBatchedStatements")) {
            String url = dbInfo.getUrl();
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?") + 1);
                url += UrlSuffix;
            } else {
                url = url + "?" + UrlSuffix;
            }
            dbInfo.setUrl(url);
        }
        return dbInfo.getUrl();
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
