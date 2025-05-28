package com.github.pdaodao.springwebplus.tool.db.dialect.doris;

import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDDLGen;

public class DorisDDLGen extends MysqlDDLGen {
    public DorisDDLGen(DbDialect dbDialect) {
        super(dbDialect);
    }
}
