package com.github.pdaodao.springwebplus.tool.io.jdbc;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.db.core.*;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFactory;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbMetaLoader;
import com.github.pdaodao.springwebplus.tool.db.pojo.SqlCmd;
import com.github.pdaodao.springwebplus.tool.db.util.DbUtil;
import com.github.pdaodao.springwebplus.tool.io.Writer;
import com.github.pdaodao.springwebplus.tool.io.lang.CdcBatchData;
import com.github.pdaodao.springwebplus.tool.io.pojo.WriteModeEnum;
import com.github.pdaodao.springwebplus.tool.lang.ConnectionProviderFactory;
import com.github.pdaodao.springwebplus.tool.lang.JdbcConnectionProvider;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import java.sql.SQLException;
import java.util.*;

/**
 * jdbc 单表写
 */
public class JdbcWriter implements Writer {
    private final DbInfo dbInfo;
    private final String tableName;
    private final WriteModeEnum writeMode;
    private List<TableColumn> fields;

    private JdbcConnectionProvider connectionProvider;

    private int batchSize = 1000;

    private transient long total = 0;
    private transient int batchCounter = 0;
    private transient CdcBatchData cdcBatchData = new CdcBatchData();

    public JdbcWriter(WriteModeEnum writeMode, DbInfo dbInfo, String tableName, List<TableColumn> fields) {
        this.writeMode = writeMode;
        this.dbInfo = dbInfo;
        this.tableName = tableName;
        this.fields = fields;
        if (DbType.Mysql == dbInfo.getDbType() && !dbInfo.getUrl().contains("rewriteBatchedStatements")) {
            final String p = "useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true&useServerPrepStmts=true&useCompression=true";
            String url = dbInfo.getUrl();
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?") + 1);
                url += p;
            } else {
                url = url + "?" + p;
            }
            dbInfo.setUrl(url);
        }
    }

    @Override
    public void open() throws Exception {
        connectionProvider = ConnectionProviderFactory.of(dbInfo, true);
        final DbMetaLoader dbMetaLoader = connectionProvider.getDialect().metaLoader(dbInfo);
        // 表结构
        TableInfo tableInfo = dbMetaLoader.tableInfo(tableName, dbInfo.getDbSchema());
        if(tableInfo == null){
            tableInfo = new TableInfo();
            tableInfo.setName(tableName);
            tableInfo.setColumns(fields);
            tableInfo.setDbSchema(dbInfo.getDbSchema());
            // 自动创建表
            final List<String> sqls = connectionProvider.getDialect().ddlGen().createTable(tableInfo);
            DbUtil.executeSqlBlock(DbUtil.getDatasource(dbInfo), SqlCmd.of(sqls));
        }else{
            final Map<String, TableColumn> fieldMap = tableInfo.fieldMap();
            for(final TableColumn f: fields){
                final TableColumn old = fieldMap.get(f.getName());
                Preconditions.checkNotNull(old, "field {} not exist.", f.getName());
                f.setDataType(old.getDataType());
                f.setTypeName(old.getTypeName());
            }
        }
    }

    @Override
    public Long total() {
        return total;
    }

    @Override
    public void write(final StreamRow row) throws Exception {
        if (row == null) {
            return;
        }
        total++;
        cdcBatchData.add(row);
        batchCounter++;
        commit(false);
    }

    @Override
    public List<TableColumn> fields() {
        return fields;
    }


    @Override
    public void close() throws Exception {
        try {
            commit(true);
            processAutoIdRestart();
        } finally {
          //  ps.close();
          //  connection.close();
        }
    }

    private synchronized void processAutoIdRestart() throws Exception{
        TableColumn pk = null;
        for(final TableColumn f: fields){
            if(BooleanUtil.isTrue(f.getIsAuto())){
                pk = f;
                break;
            }
        }
        if(pk == null){
            return;
        }
        final DbDialect dialect = DbFactory.of(dbInfo.getDbType());
        final String restartSql = dialect.setAutoIdStartSql(dbInfo.getDbSchema(), tableName, pk.getName());
        if(StrUtil.isNotBlank(restartSql)){
            DbUtil.execute(connectionProvider, restartSql);
        }
    }

    private synchronized void commit(boolean force) throws SQLException {
        if (batchCounter < 1) {
            return;
        }
        if (force == false && batchCounter < batchSize) {
            return;
        }
        batchCounter = 0;
        final CdcBatchData batched = cdcBatchData;
        cdcBatchData = new CdcBatchData();
        final TableInfo tableInfo = new TableInfo();
        tableInfo.setName(tableName);
        tableInfo.setColumns(fields);
        DbUtil.executeBatch(connectionProvider, batched, tableInfo);
    }
}
