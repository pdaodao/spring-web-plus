package com.github.pdaodao.springwebplus.tool.flow;

import com.github.pdaodao.springwebplus.tool.flow.base.NodeData;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowNode;
import com.github.pdaodao.springwebplus.tool.flow.base.FlowContext;

/**
 * 节点执行
 */
public interface NodeProcessor {
    /**
     * 构建
     * @param node
     * @param nodeData
     * @param flowContext
     */
    void build(final FlowNode node, final NodeData nodeData, final FlowContext flowContext);

    /**
     *
     * @param node
     * @param nodeData
     * @param flowContext
     * @return    -1: 表示少量有限数据行数 -2 表示大量有限数据行数 -3 表示无限数据流
     */
    default long open(final FlowNode node, final NodeData nodeData, final FlowContext flowContext){
        return -1l;
    }
}