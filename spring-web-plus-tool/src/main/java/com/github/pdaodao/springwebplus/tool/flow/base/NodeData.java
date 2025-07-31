package com.github.pdaodao.springwebplus.tool.flow.base;

import com.github.pdaodao.springwebplus.tool.flow.Constants;
import com.github.pdaodao.springwebplus.tool.sql.SqlFrame;
import lombok.Data;

import java.util.LinkedHashMap;

/**
 * 节点数据
 */
@Data
public class NodeData {
    // 构建阶段
    private BuildStage buildStage;

    private SqlFrame sqlFrame;

    private LinkedHashMap<String, ValueHolder> params = new LinkedHashMap<>();

    public ValueHolder get(final String key){
        return params.get(key);
    }

    public ValueHolder outputData(){
        return params.get(Constants.OutputNodeId);
    }

    public NodeData putOutput(final ValueHolder vh){
        params.put(Constants.OutputNodeId, vh);
        return this;
    }

    public NodeData set(final String key, final ValueHolder valueHolder){
        params.put(key, valueHolder);
        return this;
    }
}