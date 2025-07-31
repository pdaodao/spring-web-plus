package com.github.pdaodao.springwebplus.tool.flow.base;

import com.github.pdaodao.springwebplus.tool.flow.Flow;
import com.github.pdaodao.springwebplus.tool.flow.NodeProcessor;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowNode;
import com.github.pdaodao.springwebplus.tool.sql.TableAliasCounter;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class FlowContext {
    private final Flow flow;
    private final Map<String, NodeData> nodeDataMap = new ConcurrentHashMap<>();

    private final TableAliasCounter tableAliasCounter = new TableAliasCounter();

    private final Map<String, NodeProcessor> processorMap = new ConcurrentHashMap<>();

    public FlowContext(Flow flow) {
        this.flow = flow;
    }

    public FlowContext putNodeData(final String id, final NodeData nodeData){
        nodeDataMap.put(id, nodeData);
        return this;
    }

    public FlowContext putProcessor(final String id, final NodeProcessor p){
        processorMap.put(id, p);
        return this;
    }

    public NodeData getNodeData(final String id){
        return nodeDataMap.get(id);
    }

    public List<FlowNode> parentNodes(final FlowNode flowNode, int size){
        return flow.checkParentSize(flowNode, size);
    }

    public FlowNode parentNode(final FlowNode flowNode){
        final List<FlowNode> p = flow.checkParentSize(flowNode, 1);
        return p.get(0);
    }

    public List<FlowNode> childNodes(final String nodeId){
        return flow.childNodes(nodeId);
    }
}