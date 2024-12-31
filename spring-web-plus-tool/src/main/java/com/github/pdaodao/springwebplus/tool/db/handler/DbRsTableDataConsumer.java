package com.github.pdaodao.springwebplus.tool.db.handler;

import com.github.pdaodao.springwebplus.tool.data.PageInfo;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import com.github.pdaodao.springwebplus.tool.data.TableDataRow;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;

import java.util.List;

/**
 * 数据集 收集器
 */
public class DbRsTableDataConsumer implements DbRsConsumer {
    private final TableData tableData = new TableData();

    @Override
    public void fields(List<TableColumn> fields) {
        tableData.setColumns(fields);
    }

    @Override
    public void row(TableDataRow row) {
        tableData.add(row);
    }

    public TableData getData() {
        return tableData;
    }

    @Override
    public void setTotal(Long total) {
        if (tableData.getPageInfo() == null) {
            tableData.setPageInfo(new PageInfo());
        }
        tableData.getPageInfo().setTotal(total);
    }
}
