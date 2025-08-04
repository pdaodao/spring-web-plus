package com.github.pdaodao.flow.processor;

import com.github.pdaodao.springwebplus.tool.flow.NodeProcessor;
import com.github.pdaodao.springwebplus.tool.flow.base.FlowContext;
import com.github.pdaodao.springwebplus.tool.flow.base.NodeData;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowNode;

/**
 * 审批节点
 */
public class FlowApproveNode implements NodeProcessor {
    @Override
    public void build(FlowNode node, NodeData nodeData, FlowContext flowContext) {

    }

    @Override
    public long open(FlowNode node, NodeData nodeData, FlowContext flowContext) {
        return 1;
    }
}
