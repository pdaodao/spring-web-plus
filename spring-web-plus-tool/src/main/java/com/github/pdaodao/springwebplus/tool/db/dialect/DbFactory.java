package com.github.pdaodao.springwebplus.tool.db.dialect;

import com.github.pdaodao.springwebplus.tool.db.core.DbType;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.sqlite.SqliteDialect;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DbFactory {
    private static Map<DbType, DbDialect> map = new HashMap<>();

    static {
        map.put(DbType.Mysql, new MysqlDialect());
        map.put(DbType.Sqlite, new SqliteDialect());
    }

    public static DbDialect of(final DbType dbType) {
        Preconditions.checkNotNull(dbType, "db-type is null.");
        final DbDialect dialect = map.get(dbType);
        Preconditions.checkNotNull(dialect, "db-dialect is null for {}", dbType.name());
        return dialect;
    }

    public static DbDialect of(final String url) {
        return of(DbType.of(url));
    }

    public static DbDialect of(final DataSource ds) {
        return of(DbType.of(ds));
    }
}
