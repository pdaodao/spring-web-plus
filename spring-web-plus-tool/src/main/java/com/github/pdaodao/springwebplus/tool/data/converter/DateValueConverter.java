package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;

import java.time.LocalDateTime;

public class DateValueConverter implements ValueConverter<LocalDateTime>{
    @Override
    public LocalDateTime get(Object obj) {
        return DataValueUtil.toLocalDate(obj);
    }
}
