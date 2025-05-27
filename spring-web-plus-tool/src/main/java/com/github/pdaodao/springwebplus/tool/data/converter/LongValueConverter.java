package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;

public class LongValueConverter implements ValueConverter<Long>{
    @Override
    public Long get(Object obj) {
        return DataValueUtil.toLong(obj);
    }
}
