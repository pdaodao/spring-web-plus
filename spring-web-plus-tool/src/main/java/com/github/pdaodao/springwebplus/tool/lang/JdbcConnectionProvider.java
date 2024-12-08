package com.github.pdaodao.springwebplus.tool.lang;

import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * jdbc 数据库连接提供者
 */
public interface JdbcConnectionProvider {
    /**
     * 获取数据库连接
     */
    Connection getConnection() throws SQLException;

    /**
     * 获取数据库基础方言
     *
     * @return
     */
    DbDialect getDialect();

    DbInfo getDbInfo();

    default ThreadContextClassLoader getClassLoader() {
        return PluginClassLoaderFactory.threadContextOf(getDbInfo().getDbType().name());
    }
}