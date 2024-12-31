package com.github.pdaodao.springwebplus.tool.io.jdbc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.db.JdbcSqlExecutor;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.handler.JdbcUtils;
import com.github.pdaodao.springwebplus.tool.db.util.DbMetaUtil;
import com.github.pdaodao.springwebplus.tool.db.util.SqlUtil;
import com.github.pdaodao.springwebplus.tool.io.Reader;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

public class JdbcReader implements Reader {
    private final DbInfo dbInfo;
    private String sql;
    private String tableName;

    private transient Long total;
    private transient Connection connection;
    private transient PreparedStatement ps;
    private transient ResultSet rs;
    private transient List<TableColumn> fields;

    public JdbcReader(DbInfo dbInfo, String sql) {
        this.dbInfo = dbInfo;
        this.sql = sql;
    }

    @Override
    public void open() throws Exception {
        Preconditions.checkNotNull(dbInfo, "数据库连接信息为空.");
        final JdbcSqlExecutor executor = JdbcSqlExecutor.of(dbInfo);
        if (StrUtil.isBlank(sql)) {
            final List<String> fs = CollUtil.isEmpty(fields) ? ListUtil.list(false, "*") : fields.stream().map(t -> t.getName()).collect(Collectors.toList());
            sql = SqlUtil.genQuerySql(executor.getDialect(), tableName, fs);
        }
        Preconditions.checkNotBlank(sql, "sql语句为空");
        final String countSql = SqlUtil.countSql(sql);
        total = executor.queryForLong(countSql, null);
        if (total < 1) {
            return;
        }
        connection = executor.getProvider().getConnection();
        try {
            ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            if (executor.getDialect() != null) {
                ps.setFetchSize(executor.getDialect().fetchSize());
            }
            rs = ps.executeQuery();
            fields = DbMetaUtil.parseFieldsByData(rs, executor.getDialect());
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    @Override
    public Long total() {
        return total;
    }

    @Override
    public List<TableColumn> fields() {
        return fields;
    }

    @Override
    public StreamRow read() throws Exception {
        if (!rs.next()) {
            return null;
        }
        final StreamRow row = StreamRow.of(fields.size());
        for (int i = 1; i <= fields.size(); i++) {
            row.setField(fields.get(i - 1).getName(), JdbcUtils.getResultSetValue(rs, i));
        }
        return row;
    }

    @Override
    public void close() throws Exception {
        IoUtil.close(rs);
        IoUtil.close(ps);
        IoUtil.close(connection);
    }
}
