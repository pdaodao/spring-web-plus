package com.github.pdaodao.springwebplus.tool.flow.processor.groupagg;

import com.github.pdaodao.springwebplus.tool.flow.base.NodeData;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowNode;
import com.github.pdaodao.springwebplus.tool.flow.base.FlowContext;
import com.github.pdaodao.springwebplus.tool.flow.NodeProcessor;
import com.github.pdaodao.springwebplus.tool.sql.SqlFrame;
import com.github.pdaodao.springwebplus.tool.sql.core.NameAlias;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
public class GroupAgg implements NodeProcessor {

    @Override
    public void build(FlowNode node, NodeData nodeData, FlowContext flowContext) {
        final FlowNode pNode = flowContext.parentNode(node);
        final NodeData pData = flowContext.getNodeData(pNode.getId());
        final GroupAggInfo aggInfo = new GroupAggInfo();
        BeanUtils.copyProperties(node.getOptions(), aggInfo);

        if(pData.getSqlFrame() != null && pData.getSqlFrame().getSql() != null){
            final SqlFrame sqlFrame = pData.getSqlFrame().cloneToChild(node.getId());
            for(final GroupAggInfo.GroupTableColumn k: aggInfo.getKeys()){
                sqlFrame.getSql().groupBy(k.getName());
            }
            for(final GroupAggInfo.GroupTableColumn g: aggInfo.getKeys()){
                sqlFrame.getSql().select(NameAlias.of(g.getFn()+"("+g.getName()+")", g.getAlias()));
            }
            nodeData.setSqlFrame(sqlFrame);
        }
    }
}
