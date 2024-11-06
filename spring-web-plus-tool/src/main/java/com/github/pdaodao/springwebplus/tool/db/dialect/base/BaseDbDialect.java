package com.github.pdaodao.springwebplus.tool.db.dialect.base;

import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbMetaLoader;

public abstract class BaseDbDialect implements DbDialect {

    @Override
    public DbMetaLoader metaLoader(DbInfo dbInfo) {
        return new JdbcMetaLoader(dbInfo, this);
    }
}
