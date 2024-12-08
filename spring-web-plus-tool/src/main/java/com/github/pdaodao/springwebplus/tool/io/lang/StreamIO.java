package com.github.pdaodao.springwebplus.tool.io.lang;

import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;

import java.util.List;

public interface StreamIO extends AutoCloseable {
    /**
     * 获取字段列表
     *
     * @return
     */
    List<TableColumn> fields();

    /**
     * 初始化
     *
     * @throws Exception
     */
    void open() throws Exception;

    /**
     * 获取总数据行数
     *
     * @return
     */
    Long total();

}