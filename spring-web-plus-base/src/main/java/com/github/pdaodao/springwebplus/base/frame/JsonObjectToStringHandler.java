package com.github.pdaodao.springwebplus.base.frame;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;

public class JsonObjectToStringHandler extends AbstractJsonTypeHandler<Object> {

    public JsonObjectToStringHandler(Class<?> type) {
        super(type);
    }

    @Override
    public Object parse(String json) {
        if(StrUtil.isBlank(json)){
            return null;
        }
        try{
            return JsonUtil.objectMapper.readTree(json);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toJson(Object obj) {
        if(obj == null){
            return null;
        }
        return JsonUtil.toJsonString(obj);
    }
}
