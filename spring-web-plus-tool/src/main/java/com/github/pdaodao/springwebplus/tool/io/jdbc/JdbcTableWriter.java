package com.github.pdaodao.springwebplus.tool.io.jdbc;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.BatchRowMap;
import com.github.pdaodao.springwebplus.tool.data.BatchRowList;
import com.github.pdaodao.springwebplus.tool.data.TableRow;
import com.github.pdaodao.springwebplus.tool.db.JdbcBatchRunner;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.table.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFactory;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbMetaLoader;
import com.github.pdaodao.springwebplus.tool.db.DbUtil;
import com.github.pdaodao.springwebplus.tool.io.Writer;
import com.github.pdaodao.springwebplus.tool.io.pojo.WriterInfo;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

/**
 * jdbc数据表写数据
 */
@Slf4j
public class JdbcTableWriter implements Writer {
    private final WriterInfo writerInfo;
    private transient long total = 0;
    private transient DbDialect dbDialect;
    private transient BatchRowMap cdcBatchData;
    private transient JdbcBatchRunner batchRunner;
    private long lastCommitTime = 0l;

    public JdbcTableWriter(final WriterInfo writerInfo) {
        Preconditions.checkNotNull(writerInfo, "JdbcTableWriter writerInfo is null.");
        writerInfo.check();
        this.writerInfo = writerInfo;
        if(writerInfo.getBatchSize() < 1){
            writerInfo.setBatchSize(2000);
        }
    }

    @Override
    public synchronized void open() throws Exception {
        dbDialect = DbFactory.of(writerInfo.getDbInfo().getDbType());
        dbDialect.buildUrl(writerInfo.getDbInfo());
        //1. 初始化加载表结构
        final DbMetaLoader dbMetaLoader = dbDialect.metaLoader(writerInfo.getDbInfo());
        TableInfo tableInfo = dbMetaLoader.tableInfo(writerInfo.getTableName(), writerInfo.getDbInfo().getDbSchema());
        Preconditions.assertTrue(!BooleanUtil.isTrue(writerInfo.getAutoCreateTable()) && tableInfo == null, "JdbcTableWriter table: {} not exists.", writerInfo.getTableName());
        if(tableInfo == null){
            tableInfo = new TableInfo();
            tableInfo.setName(writerInfo.getTableName());
            tableInfo.setFields(writerInfo.getFields());
            tableInfo.setDbSchema(writerInfo.getDbInfo().getDbSchema());
            final List<String> sqls = dbDialect.ddlGen().createTable(tableInfo);
            log.info(StrUtil.join(";", sqls));
            DbUtil.executeSqlBlock(DbUtil.getDatasource(writerInfo.getDbInfo()), sqls);
        }
        final Map<String, TableField> fieldMap = tableInfo.fieldMap();
        for(final TableField f: writerInfo.getFields()){
            final TableField old = fieldMap.get(f.getName());
            Preconditions.checkNotNull(old, "field {} not exist.", f.getName());
            f.setDataType(old.getDataType());
            f.setIsAuto(old.getIsAuto());
            f.setIsPk(old.getIsPk());
            f.setTypeName(old.getTypeName());
            if(StrUtil.isBlank(f.getFrom())){
                f.setFrom(f.getName());
            }
        }
        tableInfo.setFields(writerInfo.getFields());
        cdcBatchData = new BatchRowMap(tableInfo.pkColumnNames());
        batchRunner = new JdbcBatchRunner(writerInfo.getDbInfo(),dbDialect, tableInfo, cdcBatchData.getPks());
    }

    @Override
    public Long total() {
        return total;
    }

    @Override
    public void write(final TableRow row) throws Exception {
        if(row == null || row.isEnd() || row.getData() == null){
            return;
        }
        cdcBatchData.add(row);
        total++;
        if(cdcBatchData.getSize() >= writerInfo.getBatchSize()){
            commit();
        }
    }

    public synchronized void commit() throws Exception{
        lastCommitTime = DateTimeUtil.currentTimeMillis();
        if(cdcBatchData.getSize() < 1){
            return;
        }
        final BatchRowList list = cdcBatchData.toList();
        batchRunner.execute(list);
    }

    @Override
    public void flush() throws Exception {
        if(DateTimeUtil.currentTimeMillis() - lastCommitTime < 3 * 1000){
            return;
        }
        commit();
    }

    @Override
    public void close() throws Exception {
        commit();
        processAutoIdRestart();
    }

    private synchronized void processAutoIdRestart() throws Exception{
        TableField pk = null;
        for(final TableField f: writerInfo.getFields()){
            if(BooleanUtil.isTrue(f.getIsAuto())){
                pk = f;
                break;
            }
        }
        if(pk == null){
            return;
        }
        final String restartSql = dbDialect.setAutoIdStartSql(writerInfo.getDbInfo().getDbSchema(), writerInfo.getTableName(), pk.getName());
        if(StrUtil.isNotBlank(restartSql)){
            DbUtil.execute(DbUtil.getDatasource(writerInfo.getDbInfo()), restartSql);
        }
    }

    @Override
    public List<TableField> fields() {
        return writerInfo.getFields();
    }
}
