package com.github.apengda.springwebplus.starter.db.dialect.sqlite;

import com.github.apengda.springwebplus.starter.db.dialect.DbDialect;
import com.github.apengda.springwebplus.starter.db.dialect.base.BaseDDLGen;
import com.github.apengda.springwebplus.starter.db.pojo.DDLBuildContext;
import com.github.apengda.springwebplus.starter.db.pojo.TableInfo;

public class SqliteDDLGen extends BaseDDLGen {

    public SqliteDDLGen(DbDialect dbDialect) {
        super(dbDialect);
    }

    @Override
    protected String genDDLTableComment(TableInfo tableInfo, DDLBuildContext ddlBuildContext) {
        return null;
    }
}
