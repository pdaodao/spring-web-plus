package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;

import java.util.List;

public interface ValueConverter<T> {
    T get(final Object obj);

    static ValueConverter[] of(final List<TableColumn> fs){
        final ValueConverter[] ret = new ValueConverter[fs.size()];
        int index = 0;
        for(final TableColumn f: fs){
            ret[index++] = of(f);
        }
        return ret;
    }

    static ValueConverter of(TableColumn f){
        if(f == null){
            return null;
        }
        if(f.getDataType() == null){
            return new ObjectValueConverter();
        }
        if(f.getDataType().isDateFamily()){
            return new DateValueConverter();
        }
        if(DataType.BOOLEAN == f.getDataType()){
            return new BooleanValueConverter();
        }
        if(f.getDataType().isDoubleFamily()){
            return new DoubleValueConverter();
        }
        if(f.getDataType().isIntFamily()){
            return new LongValueConverter();
        }
        if(f.getDataType().isStringFamily()){
            return new StrValueConverter();
        }
        return new ObjectValueConverter();
    }
}
