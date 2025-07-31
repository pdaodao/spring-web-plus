package com.github.pdaodao.springwebplus.tool.flow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowEdge;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowMode;
import com.github.pdaodao.springwebplus.tool.flow.core.FlowNode;
import com.github.pdaodao.springwebplus.tool.flow.core.NodeType;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Schema(description = "数据流程")
public class Flow {
    // id
    private String id;
    // 标题
    private String title;

    // 运行模式
    private FlowMode mode;

    @Schema(description = "节点")
    private List<FlowNode> nodes;

    @Schema(description = "连线")
    private List<FlowEdge> edges;

    @JsonIgnoreProperties
    public AtomicInteger counter = new AtomicInteger(1);

    public static Flow of(final String id, final String title) {
        final Flow f = new Flow();
        f.setId(id);
        f.setTitle(title);
        return f;
    }

    public FlowNode start() {
        final FlowNode start = read("start");
        start.setType(NodeType.start);
        start.setTitle("开始");
        return start;
    }

    public FlowNode read(final String componentCode) {
        final FlowNode flowNode = new FlowNode(nextNodeId(), componentCode);
        flowNode.setType(NodeType.source);
        flowNode.setName(componentCode);
        flowNode.setFlow(this);
        addNode(flowNode);
        return flowNode;
    }

    public FlowNode of(final NodeType nodeType, final String name){
        final FlowNode t = new FlowNode();
        t.setId(nextNodeId());
        t.setType(nodeType);
        t.setName(name);
        t.setFlow(this);
        addNode(t);
        return t;
    }

    public String nextNodeId() {
        return StrUtil.toString(counter.getAndIncrement());
    }

    public Flow addNode(final FlowNode n) {
        if (n == null) {
            return this;
        }
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        if (StrUtil.isNotBlank(n.getId())) {
            for (final FlowNode nn : nodes) {
                if (StrUtil.equals(nn.getId(), n.getId())) {
                    return this;
                }
            }
        }
        nodes.add(n);
        return this;
    }

    public Flow addEdge(final String source, final String target) {
        if (StrUtil.isBlank(source) || StrUtil.isBlank(target)) {
            return this;
        }
        if (edges == null) {
            edges = new ArrayList<>();
        }
        for (final FlowEdge e : edges) {
            if (StrUtil.equals(e.getSource(), source) && StrUtil.equals(e.getTarget(), target)) {
                return this;
            }
        }
        final FlowEdge edge = new FlowEdge();
        edge.setId(source + "_" + target);
        edge.setSource(source);
        edge.setTarget(target);
        edges.add(edge);
        return this;
    }

    /**
     * 根节点/起始节点
     * @return
     */
    public List<FlowNode> rootNodes(){
        if(CollUtil.isEmpty(nodes)){
            return null;
        }
        final Set<String> targetSourceSet = new LinkedHashSet<>();
        for(final FlowEdge e: edges){
            targetSourceSet.add(e.getTarget());
        }
        final List<FlowNode> ret = new ArrayList<>();
        for(final FlowNode n: nodes){
            if(!targetSourceSet.contains(n.getId())){
                ret.add(n);
            }
        }
        return ret;
    }

    /**
     * 父节点
     * @return
     */
    public List<FlowNode> parentNodes(final String nodeId){
        if(CollUtil.isEmpty(nodes)){
            return null;
        }
        final Set<String> sourceNodeIds = new LinkedHashSet<>();
        for(final FlowEdge e: edges){
            if(StrUtil.equals(nodeId, e.getTarget())){
                sourceNodeIds.add(e.getSource());
            }
        }
        final List<FlowNode> ret = new ArrayList<>();
        for(final FlowNode n: nodes){
            if(sourceNodeIds.contains(n.getId())){
                ret.add(n);
            }
        }
        return ret;
    }

    /**
     * 子节点
     * @return
     */
    public List<FlowNode> childNodes(final String nodeId){
        if(CollUtil.isEmpty(nodes)){
            return null;
        }
        final Set<String> targetNodeIds = new LinkedHashSet<>();
        for(final FlowEdge e: edges){
            if(StrUtil.equals(nodeId, e.getSource())){
                targetNodeIds.add(e.getTarget());
            }
        }
        final List<FlowNode> ret = new ArrayList<>();
        for(final FlowNode n: nodes){
            if(targetNodeIds.contains(n.getId())){
                ret.add(n);
            }
        }
        return ret;
    }

    public List<FlowNode>  checkParentSize(final FlowNode node, int size){
        final List<FlowNode> p = parentNodes(node.getId());
        if(size < 0){
            return p;
        }
        if(size == 0){
            Preconditions.assertTrue(CollUtil.size(p) > 0, "节点{}不能有上级节点", node.getTitle());
            return p;
        }
        Preconditions.assertTrue(CollUtil.isEmpty(p), "{}的上级节点为空.", node.getId());
        Preconditions.checkArgument(CollUtil.size(p) == size, "节点{}需要{}个上游节点", node.getTitle(), size);
        return p;
    }

    @Override
    public String toString() {
        String sb = "DataFlow{" + "id=" + id +
                ", title='" + title + '\'' +
                ", mode=" + mode +
                '}';
        return sb;
    }
}
