package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.data.TableRow;
import com.github.pdaodao.springwebplus.tool.table.TableField;

public class ValueConverterRowSetter<T> implements TableRowSetter {
    private final String name;
    private final ValueConverter<T> converter;

    public ValueConverterRowSetter(String name, ValueConverter<T> converter) {
        this.name = name;
        this.converter = converter;
    }

    public ValueConverterRowSetter(final TableField tableColumn) {
        this.name = tableColumn.getName();
        this.converter = ValueConverter.of(tableColumn);
    }

    @Override
    public void set(TableRow row, Object object) {
        row.setField(name, converter.get(object));
    }
}
