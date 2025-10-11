package com.github.pdaodao.springwebplus.tool.db.dialect.kingbase;

import com.github.pdaodao.springwebplus.tool.db.dialect.pg.PgDialect;

public class KingbaseDialect extends PgDialect {

    @Override
    public String driverName() {
        return "com.kingbase8.Driver";
    }

    @Override
    protected String buildUrlDriverName() {
        return "kingbase8";
    }
}
