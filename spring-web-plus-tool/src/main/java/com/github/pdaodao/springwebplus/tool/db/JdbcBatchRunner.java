package com.github.pdaodao.springwebplus.tool.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.BatchRowList;
import com.github.pdaodao.springwebplus.tool.data.TableRow;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import com.github.pdaodao.springwebplus.tool.table.DbType;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.table.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFactory;
import com.github.pdaodao.springwebplus.tool.sql.util.SqlUtil;
import com.github.pdaodao.springwebplus.tool.io.jdbc.support.PsSetter;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JdbcBatchRunner {
    private final DbInfo dbInfo;
    private final DbDialect dbDialect;
    private final TableInfo tableInfo;
    private List<String> pks;
    private transient String deleteSql;
    private PsSetter[] psDeleteSetters;

    private transient String upsertSql;
    private PsSetter[] psUpsertSetters;


    public JdbcBatchRunner(DbInfo dbInfo, DbDialect dbDialect, TableInfo tableInfo, List<String> pks) {
        this.dbInfo = dbInfo;
        this.dbDialect = dbDialect;
        this.tableInfo = tableInfo;
        this.pks = pks;
        open();
    }

    public JdbcBatchRunner(final DbInfo dbInfo, final TableInfo tableInfo) {
        Preconditions.checkNotNull(dbInfo, "dbInfo is null.");
        Preconditions.checkNotNull(tableInfo, "tableInfo is null.");
        this.dbInfo = dbInfo;
        this.dbDialect = DbFactory.of(dbInfo.getDbType());
        this.tableInfo = tableInfo;
        this.pks = tableInfo.pkColumnNames();
        open();
    }

    private void open(){
        // 删除语句
        if(CollUtil.isNotEmpty(pks)){
            final List<String> ww = new ArrayList<>();
            for (final String f : pks) {
                ww.add(dbDialect.quoteIdentifier(f)+" = ?");
            }
            final String sql = StrUtil.format("DELETE FROM {} WHERE {}",
                    dbDialect.quoteIdentifier(tableInfo.getName()),
                    StrUtil.join(" AND ", ww));
            deleteSql = sql;
            psDeleteSetters = new PsSetter[CollUtil.size(pks)];
            int i = 0;
            final Set<String> pkSets = pks.stream().collect(Collectors.toSet());
            for(final TableField f: tableInfo.getFields()){
                if(pkSets.contains(f.getName())){
                    psDeleteSetters[i] = PsSetter.of(f);
                }
            }
        }
        final String insert = SqlUtil.genInsertIntoSql(dbDialect, tableInfo.getName(), tableInfo.getFields());
        upsertSql = insert;
        psUpsertSetters = PsSetter.of(tableInfo.getFields());
        if(CollUtil.isEmpty(pks)){
            return;
        }
        // upsert语句
        if(DbType.Mysql == dbDialect.dbType()){
            final List<String> upFields = new ArrayList<>();
            for(final TableField f: tableInfo.getFields()){
                if(BooleanUtil.isTrue(f.getIsAuto()) || BooleanUtil.isTrue(f.getIsPk())){
                    continue;
                }
                upFields.add(dbDialect.quoteIdentifier(f.getName()) + "= VALUES("+dbDialect.quoteIdentifier(f.getName())+")");
            }
            final String sql = StrUtil.format("{} ON DUPLICATE KEY UPDATE {}", insert,
                    StrUtil.join(",", upFields));
            upsertSql = sql;
            return;
        }
        if(DbType.Postgresql == dbDialect.dbType() || DbType.Kingbase == dbDialect.dbType()){
            final List<String> upFields = new ArrayList<>();
            for(final TableField f: tableInfo.getFields()){
                if(BooleanUtil.isTrue(f.getIsAuto()) || BooleanUtil.isTrue(f.getIsPk())){
                    continue;
                }
                upFields.add(dbDialect.quoteIdentifier(f.getName()) + "= EXCLUDED."+dbDialect.quoteIdentifier(f.getName()));
            }
            final String sql = StrUtil.format("{} ON CONFLICT ({}) DO UPDATE SET {}",
                    insert,
                    SqlUtil.joinFields(dbDialect, pks),
                    StrUtil.join(",", upFields));
            upsertSql = sql;
            return;
        }
    }

    /**
     * 批量执行数据删除和插入
     * @param listData
     * @throws Exception
     */
    public void execute(final BatchRowList listData) throws Exception{
        if(CollUtil.isEmpty(listData.getDeletes()) && CollUtil.isEmpty(listData.getUpserts())){
            return;
        }
        Preconditions.checkNotBlank(upsertSql, "upsertSql is blank.");
        try(final Connection connection = DbUtil.getDatasource(dbInfo).getConnection()){
            // 删除数据
            executeBatch(connection, listData.getDeletes(), deleteSql, psDeleteSetters);
            // 插入数据
            executeBatch(connection, listData.getUpserts(), upsertSql, psUpsertSetters);
        }
    }

    /**
     * 批量执行
     * @param connection
     * @param rows
     * @param sql
     * @param setters
     * @throws Exception
     */
    public static void executeBatch(final Connection connection, final List<TableRow> rows,
                             final String sql, final PsSetter[] setters) throws Exception{
        if(CollUtil.isEmpty(rows)){
            return;
        }
        Preconditions.checkNotBlank(sql, "batch-execute sql is null.");
        try(final PreparedStatement ps = connection.prepareStatement(sql)){
            for(final TableRow row: rows){
                int i = 1;
                for(final PsSetter p: setters){
                    final Object value = row.getField(p.getFrom());
                    p.set(ps, i, value);
                    i++;
                }
                ps.addBatch();
            }
            ps.executeBatch();
            ps.clearBatch();;
        }
    }
}