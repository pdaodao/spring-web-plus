package com.github.pdaodao.springwebplus.tool.db.dialect.base;

import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFunction;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbMetaLoader;

public abstract class BaseDbDialect implements DbDialect {

    @Override
    public DbMetaLoader metaLoader(DbInfo dbInfo) {
        return new JdbcMetaLoader(dbInfo, this);
    }

    @Override
    public DbFunction dbFunction() {
        return new BaseDbFunction();
    }
}
