package com.github.pdaodao.springwebplus.tool.io;

import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.io.lang.StreamIO;

public interface Reader extends StreamIO {
    /**
     * 读取一条数据
     * 无数据时返回 null
     *
     * @return
     * @throws Exception
     */
    StreamRow read() throws Exception;
}