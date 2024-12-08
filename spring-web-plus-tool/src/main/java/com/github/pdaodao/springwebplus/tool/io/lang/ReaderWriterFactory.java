package com.github.pdaodao.springwebplus.tool.io.lang;

import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.io.Reader;
import com.github.pdaodao.springwebplus.tool.io.Writer;
import com.github.pdaodao.springwebplus.tool.io.pojo.ReaderInfo;
import com.github.pdaodao.springwebplus.tool.io.pojo.WriterInfo;

public interface ReaderWriterFactory {
    /**
     * 顺序 小的排在前面
     *
     * @return
     */
    default int seq() {
        return 500;
    }

    boolean support(DbInfo dbInfo);

    Reader createReader(ReaderInfo readerInfo);

    Writer createWriter(WriterInfo writerInfo);
}
