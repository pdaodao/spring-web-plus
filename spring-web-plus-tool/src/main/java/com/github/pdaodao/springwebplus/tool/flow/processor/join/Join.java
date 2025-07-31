package com.github.pdaodao.springwebplus.tool.flow.processor.join;

import com.github.pdaodao.springwebplus.tool.flow.base.NodeData;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowNode;
import com.github.pdaodao.springwebplus.tool.flow.base.FlowContext;
import com.github.pdaodao.springwebplus.tool.flow.NodeProcessor;
import com.github.pdaodao.springwebplus.tool.sql.SQL;
import com.github.pdaodao.springwebplus.tool.sql.SqlFrame;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import java.util.List;

public class Join implements NodeProcessor {

    @Override
    public void build(FlowNode node, NodeData nodeData, FlowContext flowContext) {
        final List<FlowNode> pNodes = flowContext.parentNodes(node, 2);
        final SqlFrame left = flowContext.getNodeData(pNodes.get(0).getId()).getSqlFrame();
        final SqlFrame right = flowContext.getNodeData(pNodes.get(1).getId()).getSqlFrame();
        if(left.getSql() == null || right.getSql() == null){
            return;
        }
        final JoinInfo joinInfo = new JoinInfo();
        BeanUtils.copyProperties(node.getOptions(), joinInfo);

        final SqlFrame sqlFrame = left.cloneToChild(node.getId());
        sqlFrame.addParent(right);
        // todo on
        final SQL sql = sqlFrame.getSql().join(joinInfo.getJoinType(), right.getSql().clone());
        nodeData.setSqlFrame(SqlFrame.of(node.getId(), sql, sqlFrame.getParentNode()));
    }
}
