package com.github.pdaodao.springwebplus.base.frame;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;

public class JsonObjectNodeHandler extends AbstractJsonTypeHandler<ObjectNode> {
    public JsonObjectNodeHandler() {
        super(ObjectNode.class);
    }

    @Override
    public ObjectNode parse(String json) {
        if(StrUtil.isNotBlank(json)){
            return null;
        }
        try{
            return (ObjectNode)JsonUtil.objectMapper.readTree(json);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String toJson(ObjectNode obj) {
        return JsonUtil.toJsonString(obj);
    }
}
