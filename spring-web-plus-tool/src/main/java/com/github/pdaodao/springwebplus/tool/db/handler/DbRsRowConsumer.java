package com.github.pdaodao.springwebplus.tool.db.handler;

import com.github.pdaodao.springwebplus.tool.data.TableDataRow;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;

import java.util.List;

public class DbRsRowConsumer implements DbRsConsumer {
    private TableDataRow row;

    @Override
    public void fields(List<TableColumn> fields) {

    }

    @Override
    public void row(TableDataRow row) {
        this.row = row;
    }

    public TableDataRow getData() {
        return row;
    }

    @Override
    public void setTotal(Long total) {

    }
}
