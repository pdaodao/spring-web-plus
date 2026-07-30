package com.github.pdaodao.springwebplus.tool.io.pojo;

import com.github.pdaodao.springwebplus.tool.data.TableRow;
import com.github.pdaodao.springwebplus.tool.io.Writer;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import java.util.List;

public class DoNothingWriter implements Writer {
    @Override
    public void write(TableRow row) throws Exception {

    }

    @Override
    public void flush() throws Exception {

    }

    @Override
    public List<TableField> fields() {
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
    public void close() throws Exception {

    }
}
