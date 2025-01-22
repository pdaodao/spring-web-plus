package com.github.pdaodao.springwebplus.tool.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.NamedSql;
import cn.hutool.db.sql.SqlExecutor;
import com.github.pdaodao.springwebplus.tool.data.PageInfo;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import com.github.pdaodao.springwebplus.tool.data.TableDataRow;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.SqlWithMapParams;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.handler.DbRowRsHandler;
import com.github.pdaodao.springwebplus.tool.db.handler.DbRsTableDataConsumer;
import com.github.pdaodao.springwebplus.tool.db.handler.RsLongHandler;
import com.github.pdaodao.springwebplus.tool.db.util.SqlUtil;
import com.github.pdaodao.springwebplus.tool.lang.ConnectionProviderFactory;
import com.github.pdaodao.springwebplus.tool.lang.JdbcConnectionProvider;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import jdk.jshell.spi.SPIResolutionException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * jdbc sql 语句执行器
 */
@Slf4j
public class JdbcSqlExecutor implements AutoCloseable {
    public static final Long MaxBound = 1000000L;
    private final JdbcConnectionProvider provider;
    /**
     * 用于关闭执行中的任务, 这里保持线程安全
     */
    private ThreadLocal<PreparedStatement> currentPs = new ThreadLocal<>();


    public JdbcSqlExecutor(JdbcConnectionProvider provider) {
        this.provider = provider;
    }

    /**
     * 创建sql执行器
     *
     * @param dbInfo
     * @return
     */
    public static JdbcSqlExecutor of(final DbInfo dbInfo) {
        final JdbcConnectionProvider provider = ConnectionProviderFactory.of(dbInfo, true);
        Preconditions.checkNotNull(provider.getDialect(), "{}数据库方言不存在.", dbInfo.getUrl());
        if (StrUtil.isBlank(dbInfo.getUrl())) {
            provider.getDialect().buildUrl(dbInfo);
        }
        return new JdbcSqlExecutor(provider);
    }

    public JdbcConnectionProvider getProvider() {
        return provider;
    }

    public DbDialect getDialect() {
        return provider.getDialect();
    }


    /**
     * 查询得到单条数据
     * @param sql
     * @param args
     * @return
     * @throws SQLException
     */
    public TableDataRow getOne(final String sql, @Nullable Object... args) throws SQLException{
        final TableData rr = list(sql, null, args);
        if(rr == null || CollUtil.isEmpty(rr.getList())){
            return null;
        }
        Preconditions.assertTrue(CollUtil.size(rr.getList()) > 1, "expected one data but get {}", CollUtil.size(rr.getList()));
        return rr.getList().iterator().next();
    }

    /**
     * 查询单条数据
     * @param tableName
     * @param filterFieldMap
     * @return
     * @throws SQLException
     */
    public TableDataRow getOne(final String tableName, final Map<String, Object> filterFieldMap) throws SQLException{
        Preconditions.checkNotBlank(tableName, "tableName is empty.");
        Preconditions.assertTrue(MapUtil.isEmpty(filterFieldMap), "get one condition is empty");
        final SqlWithMapParams sqlWithMapParams = new SqlWithMapParams();
        final List<String> fs = new ArrayList<>();
        for(final Map.Entry<String, Object> entry: filterFieldMap.entrySet()){
            final String f = StrUtil.toUnderlineCase(entry.getKey().trim());
            final String ff = sqlWithMapParams.addParam(f, entry.getValue());
            fs.add(f+" = :"+ff);
        }
        final String sql = StrUtil.format("SELECT * FROM {} WHERE {}",
                getDialect().quoteIdentifier(tableName),
                StrUtil.join(" and ", fs));
        sqlWithMapParams.setSql(sql);
        return getOne(sqlWithMapParams);
    }

    /**
     * 查询单条数据
     * @param sqlWithMapParams
     * @return
     * @throws SQLException
     */
    public TableDataRow getOne(final SqlWithMapParams sqlWithMapParams) throws SQLException{
        final TableData rr = list(sqlWithMapParams, null);
        if(rr == null || CollUtil.isEmpty(rr.getList())){
            return null;
        }
        Preconditions.assertTrue(CollUtil.size(rr.getList()) > 1, "expected one data but get {}", CollUtil.size(rr.getList()));
        return rr.getList().iterator().next();
    }

    /**
     * 查询得到列表数据
     * @param sqlWithMapParams
     * @param pageInfo
     * @return
     * @throws SQLException
     */
    public TableData list(final SqlWithMapParams sqlWithMapParams, @Nullable PageInfo pageInfo) throws SQLException {
        Preconditions.checkNotNull(sqlWithMapParams, "sql语句为空");
        final NamedSql namedSql = sqlWithMapParams.toNamedSql();
        return list(namedSql.getSql(), pageInfo, namedSql.getParams());
    }

    /**
     * 查询得到列表数据
     *
     * @param sql
     * @param pageInfo 有分页信息时分页
     * @param args
     * @return
     * @throws SQLException
     */
    public TableData list(final String sql, @Nullable PageInfo pageInfo, @Nullable Object... args) throws SQLException {
        Preconditions.checkNotBlank(sql, "sql is empty");
        // 为了防止查询数据量过大 这里如果有限制时 加入查询条数 不获取总行数
        if (pageInfo == null) {
            pageInfo = new PageInfo();
            pageInfo.setPageNum(0l);
            pageInfo.setPageSize(MaxBound);
        }
        // 先执行查询 后分页 有可能可以节省一次总数查询
        final String pageSql = getDialect().pageSql(sql, pageInfo.offset(), pageInfo.getPageSize());
        log.debug("page sql:{}", pageSql);

        try (final Connection connection = provider.getConnection()) {
            final PreparedStatement ps = ps(connection, pageSql);
            final DbRsTableDataConsumer consumer = new DbRsTableDataConsumer();
            SqlExecutor.queryAndClosePs(ps, new DbRowRsHandler(consumer, MaxBound, provider.getDialect()), args);
            final TableData result = consumer.getData();
            if(pageInfo != null && result.getPageInfo() != null){
                pageInfo.setTotal(result.getPageInfo().getTotal());
            }
            result.setPageInfo(pageInfo);
            if (result.getPageInfo().getTotal() < pageInfo.getPageSize() || result.getPageInfo().getPageNum() < 1) {
                // 不用查询总数
                pageInfo.setTotal(pageInfo.offset() + result.getPageInfo().getTotal());
            } else {
                // 需要查询总数
                final String countSql = SqlUtil.countSql(sql);
                long total = queryForLong(countSql, args);
                pageInfo.setTotal(total);
            }
            return result;
        } finally {
            currentPs.set(null);
        }
    }

    /**
     * 数据表数据行数
     *
     * @param tableName
     * @return
     */
    public Long tableCount(final String tableName) throws SQLException {
        final String sql = "select count(1) as ct  from " + getDialect().quoteIdentifier(tableName);
        return queryForLong(sql, null);
    }

    /**
     * sql 执行
     *
     * @param sql
     * @param args
     * @return
     * @throws SQLException
     */
    public Boolean execute(final String sql, @Nullable Object... args) throws SQLException {
        try (final Connection connection = provider.getConnection()) {
            final PreparedStatement ps = ps(connection, sql);
            return SqlExecutor.execute(ps, args);
        } finally {
            currentPs.set(null);
        }
    }

    /**
     * 执行插入并返回主键
     *
     * @param sql
     * @param args
     * @return
     * @throws SQLException
     */
    public String insert(final String sql, @Nullable Object... args) throws SQLException {
        Preconditions.checkNotBlank(sql, "insert sql is empty.");
        try (final Connection connection = provider.getConnection()) {
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setQueryTimeout(3 * 60);
            final boolean execute = SqlExecutor.execute(ps, args);
            try (final ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getString(1);
                }
            }
            return null;
        } finally {
            currentPs.set(null);
        }
    }

    public Long insert(final String tableName, final Map<String, Object> fieldValues) throws SQLException {
        Preconditions.checkNotBlank(tableName, "tableName is empty.");
        Preconditions.assertTrue(MapUtil.isEmpty(fieldValues), "insert values is empty.");
        final SqlWithMapParams sqlParam = new SqlWithMapParams();
        final List<String> fs = new ArrayList<>();
        final List<String> vs = new ArrayList<>();
        for(final Map.Entry<String, Object> entry: fieldValues.entrySet()){
            final String f = StrUtil.toUnderlineCase(entry.getKey().trim());
            final String ff = sqlParam.addParam(f, entry.getValue());
            fs.add(f);
            vs.add(":"+ff);
        }
        final String sql = StrUtil.format("INSERT INTO {} ({}) values ({})",
                getDialect().quoteIdentifier(tableName),
                SqlUtil.joinFields(getDialect(), fs),
                StrUtil.join(",", vs));
        sqlParam.setSql(sql);
        Preconditions.checkNotBlank(sql, "insert sql is empty.");
        try (final Connection connection = provider.getConnection()) {
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setQueryTimeout(3 * 60);
            final boolean execute = SqlExecutor.execute(ps, sqlParam.toNamedSql().getParams());
            try (final ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
            return null;
        } finally {
            currentPs.set(null);
        }
    }

    public int update(final String tableName, final Map<String, Object> fieldValues, final Map<String, Object> filterValues) throws SQLException {
        Preconditions.checkNotBlank(tableName, "tableName is empty.");
        Preconditions.assertTrue(MapUtil.isEmpty(fieldValues), "update values is empty.");
        Preconditions.assertTrue(MapUtil.isEmpty(filterValues), "update values condition is empty.");

        final SqlWithMapParams sqlParam = new SqlWithMapParams();
        final List<String> updateFields = new ArrayList<>();
        final List<String> whereFields = new ArrayList<>();
        for(final Map.Entry<String, Object> entry: fieldValues.entrySet()){
            final String f = StrUtil.toUnderlineCase(entry.getKey().trim());
            final String ff = sqlParam.addParam(f, entry.getValue());
            updateFields.add(getDialect().quoteIdentifier(f) +" = :"+ff);
        }
        for(final Map.Entry<String, Object> entry: filterValues.entrySet()){
            final String f = StrUtil.toUnderlineCase(entry.getKey().trim());
            final String ff = sqlParam.addParam(f, entry.getValue());
            whereFields.add(getDialect().quoteIdentifier(f) +" = :"+ff);
        }
        final String sql = StrUtil.format("UPDATE {} SET {} WHERE {}",
                getDialect().quoteIdentifier(tableName),
                StrUtil.join(",", updateFields),
                StrUtil.join(" and ", whereFields));
        sqlParam.setSql(sql);
        return update(sqlParam);
    }


    /**
     * update
     * @param params
     * @return
     * @throws SQLException
     */
    public int update(final SqlWithMapParams params) throws SQLException{
        Preconditions.checkNotNull(params, "update sql is null.");
        final NamedSql namedSql = params.toNamedSql();
        return update(namedSql.getSql(), namedSql.getParams());
    }


    /**
     * update
     *
     * @param sql
     * @param args
     * @return
     * @throws SQLException
     */
    public int update(final String sql, @Nullable Object... args) throws SQLException {
        Preconditions.checkNotBlank(sql, "update sql is empty.");
        try (final Connection connection = provider.getConnection()) {
            final PreparedStatement ps = ps(connection, sql);
            return SqlExecutor.executeUpdate(ps, args);
        } finally {
            currentPs.set(null);
        }
    }

    /**
     * 查询返回 Long
     *
     * @param countSql
     * @param params
     * @return
     * @throws SQLException
     */
    public Long queryForLong(final String countSql, Object... params) throws SQLException {
        try (final Connection connection = provider.getConnection()) {
            final PreparedStatement ps = ps(connection, countSql);
            return SqlExecutor.queryAndClosePs(ps, new RsLongHandler(), params);
        } finally {
            currentPs.set(null);
        }
    }

    private PreparedStatement ps(final Connection connection, final String sql) throws SQLException {
        Preconditions.checkNotEmpty(sql, "sql is empty");
        final PreparedStatement ps = connection.prepareStatement(sql);
        ps.setQueryTimeout(10 * 60);
        currentPs.set(ps);
        return ps;
    }


    @Override
    public void close() throws IOException {
        if (currentPs.get() != null) {
            try {
                currentPs.get().cancel();
            } catch (SQLException e) {
                throw new IOException(e);
            } finally {
                IoUtil.close(currentPs.get());
            }
        }
    }
}
