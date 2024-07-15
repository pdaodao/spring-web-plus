package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 数据库类型
 */
public enum DbType {
    Mysql,
    Oracle,
    Postgresql,
    Sqlite,
    Sqlserver,
    Kingbase,
    Dm;

    /**
     * 通过连接url判断类型
     *
     * @param url
     * @return
     */
    public static DbType of(final String url) {
        Preconditions.checkNotBlank(url, "jdbc-url is blank.");
        for (final DbType v : values()) {
            if (url.toLowerCase().contains("jdbc:" + v.name().toLowerCase())) {
                return v;
            }
        }
        Preconditions.assertTrue(true, "unknown db-type of:{}", url);
        return null;
    }

    public static DbType of(final DataSource ds) {
        Preconditions.checkNotNull(ds, "db datasource is null.");
        try (final Connection connection = ds.getConnection()) {
            final String name = connection.getMetaData().getDatabaseProductName();
            for (final DbType v : values()) {
                if (StrUtil.similar(name.toLowerCase(), v.name().toLowerCase()) > 0.5) {
                    return v;
                }
            }
            Preconditions.assertTrue(true, "unknown db-type of:{}", name);
        } catch (Exception e) {

        }
        return null;
    }

}
