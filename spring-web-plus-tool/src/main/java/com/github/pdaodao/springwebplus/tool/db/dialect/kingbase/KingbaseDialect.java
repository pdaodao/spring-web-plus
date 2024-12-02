package com.github.pdaodao.springwebplus.tool.db.dialect.kingbase;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.pg.PgDialect;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

public class KingbaseDialect extends PgDialect {

    @Override
    public String driverName() {
        return "com.kingbase8.Driver";
    }

    @Override
    public String buildUrl(DbInfo dbInfo) {
        Preconditions.checkNotBlank(dbInfo.getHost(), "主键地址不能为空");
        Preconditions.checkNotBlank(dbInfo.getDbName(), "库名不能为空");
        Preconditions.checkNotBlank(dbInfo.getDbSchema(), "请指定schema");
        if (dbInfo.getPort() == null) {
            dbInfo.setPort(5432);
        }
        final String fmt = "jdbc:kingbase8://{}:{}/{}?currentSchema={}";
        final String url = StrUtil.format(fmt, dbInfo.getHost(), dbInfo.getPort(), dbInfo.getDbName(), dbInfo.getDbSchema());
        return url;
    }
}
