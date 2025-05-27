package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;

public class DoubleValueConverter implements ValueConverter<Double>{
    @Override
    public Double get(Object obj) {
        return DataValueUtil.toDouble(obj);
    }
}
