package com.github.pdaodao.springwebplus.tool.db.handler;

import com.github.pdaodao.springwebplus.tool.data.TableRowData;
import com.github.pdaodao.springwebplus.tool.table.TableField;

import java.sql.SQLException;
import java.util.List;

public interface DbRsConsumer {


    /**
     * 字段信息
     *
     * @param fields
     */
    void fields(List<TableField> fields);

    /**
     * 来了一行数据
     *
     * @param row
     */
    void row(TableRowData row) throws SQLException;

    /**
     * 设置总数据行数
     *
     * @param total
     */
    void setTotal(Long total);
}
