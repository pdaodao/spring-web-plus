package com.github.pdaodao.springwebplus.tool.io.csv;

import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.io.Writer;

import java.util.List;

public class CsvWriter implements Writer {

    @Override
    public List<TableColumn> fields() {
        return null;
    }

    @Override
    public void open() throws Exception {

    }

    @Override
    public Long total() {
        return null;
    }

    @Override
    public void write(StreamRow row) {

    }

    @Override
    public void close() throws Exception {

    }
}
