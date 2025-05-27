package com.github.pdaodao.springwebplus.tool.data.converter;

public class ObjectValueConverter implements ValueConverter<Object>{
    @Override
    public Object get(Object obj) {
        return obj;
    }
}
