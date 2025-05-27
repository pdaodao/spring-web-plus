package com.github.pdaodao.springwebplus.tool.data.converter;

import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;

import java.util.Date;

public class DateValueConverter implements ValueConverter<Date>{
    @Override
    public Date get(Object obj) {
        return DataValueUtil.toDate(obj);
    }
}
