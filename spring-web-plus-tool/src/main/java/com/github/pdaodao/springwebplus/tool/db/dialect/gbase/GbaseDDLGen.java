package com.github.pdaodao.springwebplus.tool.db.dialect.gbase;

import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDDLGen;

public class GbaseDDLGen extends MysqlDDLGen {
    public GbaseDDLGen(DbDialect dbDialect) {
        super(dbDialect);
    }
}
