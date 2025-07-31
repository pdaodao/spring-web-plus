package com.github.pdaodao.springwebplus.tool.flow.processor.orderby;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import com.github.pdaodao.springwebplus.tool.data.OrderByInfo;
import com.github.pdaodao.springwebplus.tool.flow.base.NodeData;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowNode;
import com.github.pdaodao.springwebplus.tool.flow.base.FlowContext;
import com.github.pdaodao.springwebplus.tool.flow.NodeProcessor;
import com.github.pdaodao.springwebplus.tool.sql.SqlFrame;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;

public class OrderBy implements NodeProcessor {

    @Override
    public void build(FlowNode node, NodeData nodeData, FlowContext flowContext) {
        final FlowNode pNode = flowContext.parentNode(node);
        final NodeData pData = flowContext.getNodeData(pNode.getId());
        if(pData.getSqlFrame() == null
                || pData.getSqlFrame().getSql() == null){
            return;
        }
        final OrderByInfo orderByInfo = new OrderByInfo();
        BeanUtils.copyProperties(node.getOptions(), orderByInfo);
        final SqlFrame sqlFrame = pData.getSqlFrame().cloneToChild(node.getId());
        if(CollUtil.isNotEmpty(orderByInfo.getOrderItemList())){
            for(final OrderByInfo.OrderItem item: orderByInfo.getOrderItemList()){
                sqlFrame.getSql().orderBy(item.getName()+ (BooleanUtil.isTrue(item.getAsc()) ? " asc":" desc"));
            }
        }
        nodeData.setSqlFrame(sqlFrame);
    }
}
