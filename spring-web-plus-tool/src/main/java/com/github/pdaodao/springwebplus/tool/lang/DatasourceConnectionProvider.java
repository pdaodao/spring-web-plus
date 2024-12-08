package com.github.pdaodao.springwebplus.tool.lang;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@Data
public class DatasourceConnectionProvider implements JdbcConnectionProvider {
    public static final ConcurrentHashMap<String, DataSource> DATASOURCE_MAP = new ConcurrentHashMap<>();
    private final DbInfo dbInfo;
    private final DbDialect dialect;

    public DatasourceConnectionProvider(DbInfo dbInfo, DbDialect dialect) {
        this.dbInfo = dbInfo;
        this.dialect = dialect;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try (final ThreadContextClassLoader contextClassLoader = getClassLoader()) {
            contextClassLoader.setIsSet();
            return getDatasource().getConnection();
        }
    }

    public DataSource getDatasource() {
        try (final ThreadContextClassLoader contextClassLoader = getClassLoader()) {
            final String key = dbInfo.key();
            if (!DATASOURCE_MAP.containsKey(key)) {
                synchronized (DATASOURCE_MAP) {
                    if (!DATASOURCE_MAP.containsKey(key)) {
                        HikariDataSource hi = new HikariDataSource();
                        final String url = dbInfo.getUrl();
                        hi.setJdbcUrl(dbInfo.getUrl());
                        hi.setUsername(dbInfo.getUsername());
                        hi.setPassword(dbInfo.getPassword());
                        hi.setMaximumPoolSize(30);
                        hi.setMinimumIdle(1);
                        hi.setConnectionTimeout(SECONDS.toMillis(60));
                        hi.setMaxLifetime(TimeUnit.MINUTES.toMillis(20L));
                        if (StrUtil.isNotEmpty(dbInfo.getDriver())) {
                            hi.setDriverClassName(dbInfo.getDriver());
                        }
                        if (!url.startsWith("jdbc:gbase")) {
                            hi.addDataSourceProperty("remarks", true);
                        }
                        if (url.startsWith("jdbc:mysql")) {
                            hi.addDataSourceProperty("useInformationSchema", true);
                        }
                        DATASOURCE_MAP.put(key, hi);
                    }
                }
            }
            return DATASOURCE_MAP.get(key);
        }
    }
}
