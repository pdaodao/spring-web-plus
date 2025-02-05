package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.SqlExecutor;
import com.github.pdaodao.springwebplus.tool.data.RowKind;
import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import com.github.pdaodao.springwebplus.tool.data.TableDataRow;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.SqlType;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.pojo.SqlCmd;
import com.github.pdaodao.springwebplus.tool.io.Writer;
import com.github.pdaodao.springwebplus.tool.io.lang.ReaderWriterLoader;
import com.github.pdaodao.springwebplus.tool.io.pojo.WriteModeEnum;
import com.github.pdaodao.springwebplus.tool.io.pojo.WriterInfo;
import com.github.pdaodao.springwebplus.tool.lang.PluginClassLoaderFactory;
import com.github.pdaodao.springwebplus.tool.lang.ThreadContextClassLoader;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    /**
     * 写数据表
     * @param data
     * @param dbInfo
     * @param tableInfo
     * @return
     * @throws Exception
     */
    public static long writeTable(final TableData data,  final DbInfo dbInfo,
                                  final TableInfo tableInfo) throws Exception {
        Preconditions.checkNotNull(data, "数据为空.");
        Preconditions.checkNotNull(dbInfo, "数据源不存在");
        Preconditions.checkNotNull(tableInfo, "数据表不存在.");
        Preconditions.checkArgument(CollUtil.isNotEmpty(tableInfo.getColumns()), "字段为空.");
        if(CollUtil.isEmpty(data.getList())){
            return 0l;
        }
        final WriterInfo writerInfo = new WriterInfo();
        writerInfo.setDbInfo(dbInfo);
        writerInfo.setTableName(tableInfo.getName());
        writerInfo.setWriteModeEnum(WriteModeEnum.APPEND);
        writerInfo.setFields(tableInfo.getColumns().stream().filter(t -> StrUtil.isNotBlank(t.getFrom())).collect(Collectors.toList()));
        try (final Writer writer = ReaderWriterLoader.createWriter(writerInfo)) {
             writer.open();
             for(final TableDataRow row : data.getList()){
                 writer.write(StreamRow.ofKind(RowKind.INSERT, row));
             }
             return writer.total();
         }
    }

    /**
     * 不使用连接池 直接获取数据库连接
     * @return
     * @throws SQLException
     */
    public static Connection directConnection(final DbInfo dbInfo) throws SQLException {
        try(final ThreadContextClassLoader contextClassLoader = PluginClassLoaderFactory.threadContextOf(dbInfo.getDbType().name())){
            DriverManager.setLoginTimeout(90);
//        dialect.auth(dbInfo);
            final String username = StrUtil.isBlank(dbInfo.getUsername()) ? null : dbInfo.getUsername();
            final String password = StrUtil.isBlank(dbInfo.getPassword()) ? null : dbInfo.getPassword();
            final Properties pp = new Properties();
            if(StrUtil.isNotBlank(username)){
                pp.setProperty("user", username);
            }
            if(StrUtil.isNotBlank(password)){
                pp.setProperty("password", password);
            }
            if (StrUtil.isNotBlank(dbInfo.getDriver())) {
                if(!BeanUtils.hasClass(dbInfo.getDriver())){
                    try{
                        final Class clazz = contextClassLoader.forName(dbInfo.getDriver());
                        final Driver driver = (Driver) ReflectUtil.newInstance(clazz);
                        return driver.connect(dbInfo.getUrl(), pp);
                    }catch (ClassNotFoundException e){
                        Preconditions.assertTrue(true, "数据库{}驱动{}不存在", dbInfo.getDbType(), dbInfo.getDriver());
                    }
                }
            }
            return DriverManager.getConnection(dbInfo.getUrl(), username, password);
        }
    }
}
