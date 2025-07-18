package com.github.pdaodao.springwebplus.tool.nosql;

import com.github.pdaodao.springwebplus.tool.table.DbInfo;

public interface NoSqlDataQueryFactory {

    boolean accept(DbInfo dbInfo);

    NosqlQueryExecutor create();
}
