package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.SqlExecutor;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.SqlType;
import com.github.pdaodao.springwebplus.tool.db.pojo.SqlCmd;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class DbUtil {
    private static Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    /**
     * 获取数库连接池
     *
     * @param dbInfo
     * @return
     */
    public static DataSource getDatasource(final DbInfo dbInfo) {
        Preconditions.checkNotNull(dbInfo, "dbInfo is null");
        Preconditions.checkNotBlank(dbInfo.getUrl(), "dbInfo url is empty.");
        final String key = dbInfo.key();
        DataSource ds = dataSourceMap.get(key);
        if (ds != null) {
            return ds;
        }
        synchronized (DbUtil.class) {
            ds = dataSourceMap.get(key);
            if (ds != null) {
                return ds;
            }
            final HikariDataSource hi = new HikariDataSource();
            final String url = dbInfo.getUrl();
            hi.setJdbcUrl(dbInfo.getUrl());
            hi.setUsername(dbInfo.getUsername());
            hi.setPassword(dbInfo.getPassword());
            hi.setMaximumPoolSize(30);
            hi.setMinimumIdle(1);
            hi.setConnectionTimeout(SECONDS.toMillis(90));
            hi.setMaxLifetime(TimeUnit.MINUTES.toMillis(20L));
            if (StrUtil.isNotEmpty(dbInfo.getDriver())) {
                hi.setDriverClassName(dbInfo.getDriver());
            }
            if (BooleanUtil.isTrue(dbInfo.getReadOnly())) {
                hi.setReadOnly(true);
            }
            if (!url.startsWith("jdbc:gbase")) {
                hi.addDataSourceProperty("remarks", true);
            }
            if (url.startsWith("jdbc:mysql")) {
                hi.addDataSourceProperty("useInformationSchema", true);
            }
            ds = hi;
        }
        dataSourceMap.put(key, ds);
        return ds;
    }


    public static void executeSqlBlock(final DataSource dataSource, final List<String> list) throws Exception {
        executeSqlBlock(dataSource, SqlCmd.of(list));
    }

    /**
     * 在一个事务中执行多条sql语句
     *
     * @param dataSource
     * @param sqlCmd
     * @throws Exception
     */
    public static void executeSqlBlock(final DataSource dataSource, final SqlCmd sqlCmd) throws Exception {
        if (sqlCmd == null || sqlCmd.empty()) {
            return;
        }
        final Connection connection = dataSource.getConnection();
        final boolean auto = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            if (StrUtil.isNotBlank(sqlCmd.getSql())) {
                if (SqlType.SELECT == sqlCmd.getSqlType()) {
                    try (final ResultSet rs = connection.prepareStatement(sqlCmd.getSql()).executeQuery()) {
                        if (rs.next()) {
                            return;
                        }
                    }
                } else {
                    SqlExecutor.execute(connection, sqlCmd.getSql());
                }
            }
            if (CollUtil.isEmpty(sqlCmd.getChildren())) {
                return;
            }
            for (final SqlCmd sql : sqlCmd.getChildren()) {
                if (SqlType.SELECT == sql.getSqlType()) {
                    continue;
                }
                SqlExecutor.execute(connection, sql.getSql());
            }
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(auto);
            connection.close();
        }
    }

    /**
     * 分页语句包装器
     *
     * @param sql
     * @param limitSegment
     * @return
     */
    public static String pageSqlWrap(String sql, final String limitSegment) {
        if (StrUtil.isBlank(sql) || StrUtil.isBlank(limitSegment)) {
            return sql;
        }
        final String pageKey = limitSegment.split(" ")[0];
        sql = sql.trim();
        boolean isEndWith = false;
        if (sql.endsWith(";")) {
            isEndWith = true;
            sql = sql.substring(0, sql.length() - 1);
        }
        if (StrUtil.containsIgnoreCase(sql, pageKey + " ")) {
            sql = "select * from ( " + sql + ") jzzz";
        }
        return sql + " " + limitSegment + (isEndWith ? ";" : "");
    }
}
