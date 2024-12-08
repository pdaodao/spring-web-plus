package com.github.pdaodao.springwebplus.tool.lang;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFactory;

public class ConnectionProviderFactory {

    public static JdbcConnectionProvider of(DbInfo dbInfo, boolean usePoor) {
        final DbDialect baseDialect = DbFactory.of(dbInfo.getUrl());
        if (StrUtil.isBlank(dbInfo.getDriver()) && baseDialect != null) {
            dbInfo.setDriver(baseDialect.driverName());
        }
        return new DatasourceConnectionProvider(dbInfo, baseDialect);
    }

}
