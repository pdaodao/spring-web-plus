package com.github.pdaodao.springwebplus.tool.io.jdbc;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.DbType;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.util.SqlUtil;
import com.github.pdaodao.springwebplus.tool.io.Writer;
import com.github.pdaodao.springwebplus.tool.io.pojo.WriteModeEnum;
import com.github.pdaodao.springwebplus.tool.lang.ConnectionProviderFactory;
import com.github.pdaodao.springwebplus.tool.lang.JdbcConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * jdbc 单表写
 */
public class JdbcWriter implements Writer {
    private final DbInfo dbInfo;
    private final String tableName;
    private final WriteModeEnum writeMode;
    public transient PreparedStatement ps;
    private List<TableColumn> fields;
    private int batchSize = 1000;
    private transient Connection connection;
    private transient long total = 0;
    private transient int batchCounter = 0;


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
        final JdbcConnectionProvider connectionProvider = ConnectionProviderFactory.of(dbInfo, true);
        this.connection = connectionProvider.getConnection();
        if (writeMode == null || WriteModeEnum.APPEND == writeMode || WriteModeEnum.FULL == writeMode) {
            final String sql = SqlUtil.genInsertIntoSql(connectionProvider.getDialect(), tableName, fields);
            this.ps = connection.prepareStatement(sql);
            return;
        }else if(WriteModeEnum.UPDATE == writeMode){
            TableColumn pk = null;
            final List<TableColumn> updateFields = new ArrayList<>();
            for(final TableColumn f: fields){
                if(BooleanUtil.isTrue(f.getIsPk())){
                    pk = f;
                    continue;
                }
                updateFields.add(f);
            }
            final String sql = SqlUtil.genUpdateSql(connectionProvider.getDialect(), tableName, pk, updateFields);
            this.ps = connection.prepareStatement(sql);
            fields = new ArrayList<>();
            fields.addAll(updateFields);
            fields.add(pk);
            return;
        }
        throw new UnsupportedOperationException("jdbc write not support writeMode:" + writeMode);
    }

    @Override
    public Long total() {
        return total;
    }

    @Override
    public void write(StreamRow row) throws Exception {
        if (row == null) {
            return;
        }
        batchCounter++;
        total++;
        int i = 1;
        for (final TableColumn t : fields) {
            Object value = row.getFieldAs(t.getFrom());
            if(t.getDataType() != null && t.getDataType().isDoubleFamily() && ObjectUtil.isEmpty(value)){
                value = null;
            }
            ps.setObject(i++, value);
        }
        ps.addBatch();
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
        } finally {
            ps.close();
            connection.close();
        }
    }

    private synchronized void commit(boolean force) throws SQLException {
        if (batchCounter < 1) {
            return;
        }
        if (force == false && batchCounter < batchSize) {
            return;
        }
        // 提交 insert
        ps.executeBatch();
        ps.clearBatch();
        batchCounter = 0;
    }
}
