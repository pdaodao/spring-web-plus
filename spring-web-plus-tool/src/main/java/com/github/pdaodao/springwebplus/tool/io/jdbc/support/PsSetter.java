package com.github.pdaodao.springwebplus.tool.io.jdbc.support;

import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface PsSetter<T> {

    T set(final PreparedStatement ps, final int index, final Object obj) throws SQLException;

    String getName();

    String getFrom();

    static PsSetter[] of(final List<TableField> fs){
        final PsSetter[] ret = new PsSetter[fs.size()];
        int index = 0;
        for(final TableField f: fs){
            ret[index++] = of(f);
        }
        return ret;
    }

    static PsSetter of(TableField f){
        if(f == null){
            return null;
        }
        if(f.getDataType() == null){
            return new PsObjectSetter(f.getName(), f.getFrom(), f.getDataType());
        }
        if(f.getDataType().isDateFamily()){
            return new PsDateSetter(f.getName(), f.getFrom());
        }
        if(DataType.BOOLEAN == f.getDataType()){
            return new PsBooleanSetter(f.getName(), f.getFrom());
        }
        if(f.getDataType().isStringFamily()){
            return new PsStrSetter(f.getName(), f.getFrom());
        }
        return new PsObjectSetter(f.getName(), f.getFrom(), f.getDataType());
    }
}
