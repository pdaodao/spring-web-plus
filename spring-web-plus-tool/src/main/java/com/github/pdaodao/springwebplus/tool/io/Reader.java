package com.github.pdaodao.springwebplus.tool.io;

import com.github.pdaodao.springwebplus.tool.data.TableRow;

public interface Reader extends StreamIO {
    /**
     * 读取一条数据
     * 无数据时返回 null
     *
     * @return
     * @throws Exception
     */
    TableRow read() throws Exception;
}