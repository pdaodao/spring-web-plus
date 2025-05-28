package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.data.TableRow;
import com.github.pdaodao.springwebplus.tool.data.TableRowFieldGetter;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;

public class TableRowStringGetter implements TableRowFieldGetter<String> {
    private final String from;
    private final DataType dataType;

    public TableRowStringGetter(String from, DataType dataType) {
        this.from = from;
        this.dataType = dataType;
    }

    @Override
    public String get(TableRow row) {
        final Object obj = row.get(from);
        return DataValueUtil.toString(obj, dataType);
    }
}
