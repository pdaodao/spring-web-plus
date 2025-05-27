package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.data.StreamRowValueGetter;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;

public class StreamRowStringGetter implements StreamRowValueGetter<String> {
    private final String from;
    private final DataType dataType;

    public StreamRowStringGetter(String from, DataType dataType) {
        this.from = from;
        this.dataType = dataType;
    }

    @Override
    public String get(StreamRow row) {
        final Object obj = row.get(from);
        return DataValueUtil.toString(obj, dataType);
    }
}
