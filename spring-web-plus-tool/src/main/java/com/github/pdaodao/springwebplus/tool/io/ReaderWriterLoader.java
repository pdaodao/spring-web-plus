package com.github.pdaodao.springwebplus.tool.io;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ServiceLoaderUtil;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import com.github.pdaodao.springwebplus.tool.io.lang.ReaderWriterFactory;
import com.github.pdaodao.springwebplus.tool.io.pojo.ReaderInfo;
import com.github.pdaodao.springwebplus.tool.io.pojo.WriterInfo;
import com.github.pdaodao.springwebplus.tool.lang.PluginClassLoaderFactory;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.util.List;

public class ReaderWriterLoader {

    /**
     * 创建读处理器
     *
     * @param readerInfo
     * @return
     */
    public static Reader createReader(final ReaderInfo readerInfo) {
        final List<ReaderWriterFactory> factorys = load(readerInfo.getDbInfo());
        for (final ReaderWriterFactory f : factorys) {
            if (f.support(readerInfo.getDbInfo())) {
                return f.createReader(readerInfo);
            }
        }
        throw new IllegalArgumentException("unsupport reader for db:" + readerInfo.getDbInfo().getDbType());
    }


    /**
     * 创建写处理器
     *
     * @param writerInfo
     * @return
     */
    public static Writer createWriter(final WriterInfo writerInfo) {
        final List<ReaderWriterFactory> factorys = load(writerInfo.getDbInfo());
        for (final ReaderWriterFactory f : factorys) {
            if (f.support(writerInfo.getDbInfo())) {
                return f.createWriter(writerInfo);
            }
        }
        throw new IllegalArgumentException("unsupport sink for db:" + writerInfo.getDbInfo().getUrl());
    }

    private static List<ReaderWriterFactory> load(final DbInfo dbInfo) {
        Preconditions.checkNotNull(dbInfo, "数据连接器信息为空.");
        Preconditions.checkNotNull(dbInfo, "数据数据源信息为空.");
        Preconditions.checkNotNull(dbInfo.getDbType(), "数据源类型为空");
        final ClassLoader classLoader = PluginClassLoaderFactory.of(dbInfo.getDbType());
        final List<ReaderWriterFactory> factorys = ServiceLoaderUtil.loadList(ReaderWriterFactory.class, classLoader);
        CollUtil.sort(factorys, (o1, o2) -> Integer.compare(o1.seq(), o2.seq()));
        return factorys;
    }
}
