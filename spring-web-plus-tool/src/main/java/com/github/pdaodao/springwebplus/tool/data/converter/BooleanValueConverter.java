package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;

public class BooleanValueConverter implements ValueConverter<Boolean>{
    @Override
    public Boolean get(Object obj) {
        return DataValueUtil.toBoolean(obj);
    }
}