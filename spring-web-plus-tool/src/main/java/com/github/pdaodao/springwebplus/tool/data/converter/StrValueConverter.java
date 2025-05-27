package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;

public class StrValueConverter implements ValueConverter<String>{
    @Override
    public String get(Object obj) {
        return DataValueUtil.toString(obj, DataType.STRING);
    }
}
