package com.github.pdaodao.springwebplus.tool.io.jdbc;

import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.io.Reader;
import com.github.pdaodao.springwebplus.tool.io.Writer;
import com.github.pdaodao.springwebplus.tool.io.lang.ReaderWriterFactory;
import com.github.pdaodao.springwebplus.tool.io.pojo.ReaderInfo;
import com.github.pdaodao.springwebplus.tool.io.pojo.WriterInfo;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

public class JdbcFactory implements ReaderWriterFactory {

    @Override
    public boolean support(final DbInfo dbInfo) {
        return dbInfo != null
                && dbInfo.getUrl().startsWith("jdbc");
    }

    @Override
    public Reader createReader(final ReaderInfo readerInfo) {
        final String sql = readerInfo.getSql();
        final JdbcReader jdbcReader = new JdbcReader(readerInfo.getDbInfo(), readerInfo.getTableName(), sql);
        return jdbcReader;
    }

    @Override
    public Writer createWriter(final WriterInfo writerInfo) {
        Preconditions.checkNotNull(writerInfo, "写数据连接器信息为空");
        final JdbcWriter writer = new JdbcWriter(writerInfo.getWriteModeEnum(),
                writerInfo.getDbInfo(), writerInfo.getTableName(), writerInfo.getFields());
        return writer;
    }
}
