package com.github.pdaodao.springwebplus.tool.db.dialect.base;

import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.core.TableType;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbMetaLoader;
import com.github.pdaodao.springwebplus.tool.db.util.DbMetaUtil;
import com.github.pdaodao.springwebplus.tool.db.util.DbUtil;

import javax.sql.DataSource;
import java.util.List;

/**
 * 基于 jdbc 的 meta loader
 */
public class JdbcMetaLoader implements DbMetaLoader {
    private final DbInfo dbInfo;
    private final DbDialect dbDialect;

    public JdbcMetaLoader(DbInfo dbInfo, DbDialect dbDialect) {
        this.dbInfo = dbInfo;
        this.dbDialect = dbDialect;
    }

    @Override
    public List<TableInfo> tableList(final TableType... tableTypes) throws Exception {
        final DataSource ds = DbUtil.getDatasource(dbInfo);
        return DbMetaUtil.tableList(ds, dbInfo.getDbSchema(), tableTypes);
    }

    @Override
    public TableInfo tableInfo(String tableName, String schema) throws Exception {
        final DataSource ds = DbUtil.getDatasource(dbInfo);
        return DbMetaUtil.tableInfo(ds, tableName, schema, dbDialect);
    }
}
