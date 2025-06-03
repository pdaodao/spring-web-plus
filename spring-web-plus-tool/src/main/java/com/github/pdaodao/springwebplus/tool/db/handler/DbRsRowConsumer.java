package com.github.pdaodao.springwebplus.tool.db.handler;

import com.github.pdaodao.springwebplus.tool.data.TableRowData;
import com.github.pdaodao.springwebplus.tool.table.TableField;

import java.util.List;

public class DbRsRowConsumer implements DbRsConsumer {
    private TableRowData row;

    @Override
    public void fields(List<TableField> fields) {

    }

    @Override
    public void row(TableRowData row) {
        this.row = row;
    }

    public TableRowData getData() {
        return row;
    }

    @Override
    public void setTotal(Long total) {

    }
}
