package com.github.pdaodao.springwebplus.tool.io.excel;

import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.github.pdaodao.springwebplus.tool.data.DataQueue;
import com.github.pdaodao.springwebplus.tool.data.StreamRow;

import java.util.List;

public class ExcelRowHandler implements RowHandler {
    private final DataQueue<StreamRow> queue;

    public ExcelRowHandler(DataQueue<StreamRow> queue) {
        this.queue = queue;
    }

    @Override
    public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
        final StreamRow row = StreamRow.of(rowCells.size());
        for (int index = 0; index < rowCells.size(); index++) {
            row.setField("c" + (index + 1), rowCells.get(index));
        }
        try {
            queue.put(row);
        } catch (Exception e) {
            queue.setException(e);
        }
    }
}
