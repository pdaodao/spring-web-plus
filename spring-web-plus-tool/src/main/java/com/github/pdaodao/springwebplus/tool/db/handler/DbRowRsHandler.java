package com.github.pdaodao.springwebplus.tool.db.handler;

import cn.hutool.db.handler.RsHandler;
import com.github.pdaodao.springwebplus.tool.data.TableDataRow;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.util.DbMetaUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * 消费查询结果返回 总数据行数
 */
public class DbRowRsHandler implements RsHandler<Long> {
    private final DbRsConsumer dbRsConsumer;
    private final Long bound;
    private final DbDialect dbDialect;

    public DbRowRsHandler(DbRsConsumer dbRsConsumer, Long bound, DbDialect dialect) {
        this.dbRsConsumer = dbRsConsumer;
        this.bound = bound;
        this.dbDialect = dialect;
    }

    public DbRowRsHandler(DbRsConsumer dbRsConsumer, DbDialect dialect) {
        this.dbRsConsumer = dbRsConsumer;
        this.bound = null;
        this.dbDialect = dialect;
    }

    /**
     * copy from hutool
     *
     * @param rs
     * @param columnIndex
     * @param type
     * @return
     * @throws SQLException
     */
    private static Object getColumnValue(ResultSet rs, int columnIndex, int type) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, columnIndex);
    }

    @Override
    public Long handle(ResultSet rs) throws SQLException {
        final ResultSetMetaData meta = rs.getMetaData();
        final int columnCount = meta.getColumnCount();
        final List<TableColumn> fields = DbMetaUtil.parseFields(rs, dbDialect);
        dbRsConsumer.fields(fields);
        long total = 0;
        while (rs.next()) {
            total++;
            if (bound != null && total > bound) {
                continue;
            }
            final TableDataRow row = new TableDataRow();
            for (int i = 1; i <= columnCount; i++) {
                row.put(meta.getColumnLabel(i), getColumnValue(rs, i, meta.getColumnType(i)));
            }
            dbRsConsumer.row(row);
        }
        dbRsConsumer.setTotal(total);
        return total;
    }
}
