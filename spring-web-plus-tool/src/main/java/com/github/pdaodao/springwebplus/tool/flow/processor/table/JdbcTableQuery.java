package com.github.pdaodao.springwebplus.tool.flow.processor.table;

import com.github.pdaodao.springwebplus.tool.flow.base.NodeData;
import com.github.pdaodao.springwebplus.tool.flow.base.NodeFieldDef;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowNode;
import com.github.pdaodao.springwebplus.tool.flow.base.FlowContext;
import com.github.pdaodao.springwebplus.tool.flow.NodeProcessor;
import com.github.pdaodao.springwebplus.tool.sql.SQL;
import com.github.pdaodao.springwebplus.tool.sql.SqlFrame;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;

public class JdbcTableQuery implements NodeProcessor {

    @Override
    public void build(FlowNode node, NodeData nodeData, FlowContext flowContext) {
        flowContext.parentNodes(node, 0);
        final TableQueryInfo info = new TableQueryInfo();
        BeanUtils.copyProperties(node.getOptions(), info);
        final SQL sql = SQL.of(flowContext.getTableAliasCounter())
                .from(info.getTableName());
        for(final NodeFieldDef f: node.getFields()){
            sql.select(f.getName());
        }
        final SqlFrame sqlFrame = new SqlFrame(node.getId(), sql);
        nodeData.setSqlFrame(sqlFrame);
    }
}
