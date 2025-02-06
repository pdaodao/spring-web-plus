package com.github.pdaodao.springwebplus.tool.fs;


import cn.hutool.core.io.IoUtil;

import java.io.Closeable;
import java.io.InputStream;

public class InputStreamWrap implements AutoCloseable {
    public final InputStream inputStream;
    public final Closeable client;

    // 文件名称
    public String name;

    public InputStreamWrap(InputStream inputStream, Closeable client) {
        this.inputStream = inputStream;
        this.client = client;
    }

    public static InputStreamWrap of(final InputStream in) {
        final InputStreamWrap w = new InputStreamWrap(in, null);
        return w;
    }

    public static InputStreamWrap of(final InputStream in, Closeable client) {
        final InputStreamWrap w = new InputStreamWrap(in, client);
        return w;
    }

    @Override
    public void close() {
        IoUtil.close(inputStream);
        IoUtil.close(client);
    }
}