package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;

public class ValueConverterRowSetter<T> implements StreamRowSetter{
    private final String name;
    private final ValueConverter<T> converter;

    public ValueConverterRowSetter(String name, ValueConverter<T> converter) {
        this.name = name;
        this.converter = converter;
    }

    public ValueConverterRowSetter(final TableColumn tableColumn) {
        this.name = tableColumn.getName();
        this.converter = ValueConverter.of(tableColumn);
    }

    @Override
    public void set(StreamRow row, Object object) {
        row.setField(name, converter.get(object));
    }
}
