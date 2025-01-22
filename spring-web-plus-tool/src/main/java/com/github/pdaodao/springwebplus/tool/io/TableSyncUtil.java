package com.github.pdaodao.springwebplus.tool.io;

import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.io.lang.ReaderWriterLoader;
import com.github.pdaodao.springwebplus.tool.io.pojo.ReaderInfo;
import com.github.pdaodao.springwebplus.tool.io.pojo.WriterInfo;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

public class TableSyncUtil {

    public static long tableSync(final ReaderInfo readerInfo, final WriterInfo writerInfo) throws Exception{
        Preconditions.checkNotNull(readerInfo, "读信息为空");
        Preconditions.checkNotNull(writerInfo, "写信息为空");
        final Reader reader = ReaderWriterLoader.createReader(readerInfo);
        final Writer writer = ReaderWriterLoader.createWriter(writerInfo);
        return tableSync(reader, writer);
    }


    /**
     * 同步数据表
     */
    public static long tableSync(final Reader reader, final Writer writer) throws Exception {
        Preconditions.checkNotNull(reader, "数据读处理器为空");
        Preconditions.checkNotNull(writer, "数据写处理器为空");
        try {
            writer.open();
            try {
                reader.open();
                final Long readTotal = reader.total();
                while (true) {
                    final StreamRow row = reader.read();
                    if (row == null || row.isEnd()) {
                        break;
                    }
                    writer.write(row);
                }
            } finally {
                reader.close();
            }
        } finally {
            writer.close();
        }
        return writer.total();
    }
}
