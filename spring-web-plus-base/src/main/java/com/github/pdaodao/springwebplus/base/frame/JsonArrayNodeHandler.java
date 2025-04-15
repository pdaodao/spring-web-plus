package com.github.pdaodao.springwebplus.base.frame;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;

public class JsonArrayNodeHandler extends AbstractJsonTypeHandler<ArrayNode> {
    public JsonArrayNodeHandler() {
        super(ArrayNode.class);
    }

    @Override
    public ArrayNode parse(String json) {
        if(StrUtil.isNotBlank(json)){
            return null;
        }
        try{
            return (ArrayNode)JsonUtil.objectMapper.readTree(json);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String toJson(ArrayNode obj) {
        return JsonUtil.toJsonString(obj);
    }
}
