package com.github.pdaodao.springwebplus.tool.db.dialect.base;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import com.github.pdaodao.springwebplus.tool.table.TableInfo;
import com.github.pdaodao.springwebplus.tool.table.TableType;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbMetaLoader;
import com.github.pdaodao.springwebplus.tool.db.DbMetaUtil;
import com.github.pdaodao.springwebplus.tool.db.DbUtil;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
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
    public List<String> dbList() throws Exception {
        return DbMetaUtil.dbList(DbUtil.getDatasource(dbInfo));
    }

    @Override
    public List<String> schemaList() throws SQLException {
        return DbMetaUtil.schemaList(DbUtil.getDatasource(dbInfo), dbCatalog());
    }

    protected String dbCatalog() {
        if (StrUtil.isNotBlank(dbInfo.getDbName())) {
            return dbInfo.getDbName();
        }
        return null;
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

    @Override
    public String test() throws Exception {
        if(StrUtil.isBlank(dbInfo.getUrl())){
            dbDialect.buildUrl(dbInfo);
        }
        final StringBuilder msg = new StringBuilder();
        DriverManager.setLoginTimeout(60);
        try (final Connection con = DbUtil.directConnection(dbInfo)) {
            if (con.isValid(60)) {
                DatabaseMetaData metaData = con.getMetaData();
                msg.append("数据库为:")
                        .append(metaData.getDatabaseProductName())
                        .append("  \n版本为: ")
                        .append(metaData.getDatabaseProductVersion());
            }
        }
        return msg.toString();
    }
}