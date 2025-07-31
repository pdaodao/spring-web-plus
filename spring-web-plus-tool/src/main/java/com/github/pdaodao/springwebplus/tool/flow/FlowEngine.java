package com.github.pdaodao.springwebplus.tool.flow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.flow.base.BuildStage;
import com.github.pdaodao.springwebplus.tool.flow.base.NodeData;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowNode;
import com.github.pdaodao.springwebplus.tool.flow.base.FlowContext;

import java.util.List;

public class FlowEngine {
    private final FlowContext flowContext;

    public FlowEngine(Flow flow) {
        this.flowContext = new FlowContext(flow);
    }

    public FlowEngine(Flow flow, final NodeData input) {
        this.flowContext = new FlowContext(flow);
        flowContext.putNodeData(Constants.InputNodeId, input);
    }

    public void execute(){
        //1. 构建
        build(BuildStage.build);
        //2. 预运行
        build(BuildStage.open);
        //3. 运行
    }


    private void build(final BuildStage buildStage){
        final List<FlowNode> roots = flowContext.getFlow().rootNodes();
        for(final FlowNode n: roots){
            buildNode(n, buildStage);
        }
    }

    private void buildNode(final FlowNode n, final BuildStage buildStage){
        if(flowContext.getNodeData(n.getId()) != null){
            return;
        }
        final List<FlowNode> ps = flowContext.parentNodes(n, -1);
        // 由于有两个父级的情况 这里确保所有的父级先构建
        if (CollectionUtil.isNotEmpty(ps)) {
            for (final FlowNode p : ps) {
                buildNode(p, buildStage);
            }
        }
        if(flowContext.getNodeData(n.getId()) != null
                && buildStage == flowContext.getNodeData(n.getId()).getBuildStage()){
            return;
        }
        final NodeProcessor p = ofProcessor(n.getName());
        flowContext.putProcessor(n.getId(), p);
        final NodeData nodeData = new NodeData();
        flowContext.putNodeData(n.getId(), nodeData);
        if(BuildStage.build == buildStage){
            nodeData.setBuildStage(BuildStage.build);
            p.build(n, nodeData, flowContext);
        }else if(BuildStage.open == buildStage){
            nodeData.setBuildStage(BuildStage.open);
            p.open(n, nodeData, flowContext);
        }
        final List<FlowNode> chs = flowContext.childNodes(n.getId());
        if(CollUtil.isEmpty(chs)){
            return;
        }
        for(final FlowNode ch: chs){
            buildNode(ch, buildStage);
        }
    }

    public static NodeProcessor ofProcessor(final String processor) {
        final List<NodeProcessor> list = ServiceLoaderUtil.loadList(NodeProcessor.class, Thread.currentThread().getContextClassLoader());
        for (final NodeProcessor node : list) {
            if(StrUtil.equalsIgnoreCase(processor, node.getClass().getSimpleName())){
                return node;
            }
        }
        throw new IllegalArgumentException("processor not found for:" + processor);
    }
}
