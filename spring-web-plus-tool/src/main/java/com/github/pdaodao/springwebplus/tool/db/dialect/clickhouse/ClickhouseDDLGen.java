package com.github.pdaodao.springwebplus.tool.db.dialect.clickhouse;

import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDDLGen;

public class ClickhouseDDLGen extends MysqlDDLGen {
    public ClickhouseDDLGen(DbDialect dbDialect) {
        super(dbDialect);
    }
}
