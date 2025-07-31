package com.github.pdaodao.springwebplus.tool.flow.processor.limit;

import com.github.pdaodao.springwebplus.tool.flow.base.NodeData;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowNode;
import com.github.pdaodao.springwebplus.tool.flow.base.FlowContext;
import com.github.pdaodao.springwebplus.tool.flow.NodeProcessor;
import com.github.pdaodao.springwebplus.tool.sql.SqlFrame;

public class Limit implements NodeProcessor {

    @Override
    public void build(FlowNode node, final NodeData nodeData, FlowContext flowContext) {
        final FlowNode pNode = flowContext.parentNode(node);
        final NodeData pData = flowContext.getNodeData(pNode.getId());
        final Long offset = node.getOptions().getLong("offset", 0l);
        final Long size = node.getOptions().getLong("size", 0l);
        if(pData.getSqlFrame() == null || pData.getSqlFrame().getSql() == null){
            return;
        }
        final SqlFrame sqlFrame = pData.getSqlFrame().cloneToChild(node.getId());
        sqlFrame.getSql().offset(offset).size(size);
        nodeData.setSqlFrame(sqlFrame);
    }

//    @Override
//    public NodeData process() {
//        return null;
//    }
}
