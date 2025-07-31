package com.github.pdaodao.springwebplus.tool.flow.core;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.flow.Flow;
import com.github.pdaodao.springwebplus.tool.flow.base.NodeFieldDef;
import com.github.pdaodao.springwebplus.tool.lang.OptionMap;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "节点")
public class FlowNode {
    @JsonIgnoreProperties
    private transient Flow flow;

    @Schema(description = "id")
    private String id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "组件编码")
    private String name;

    // 类型
    @Schema(description = "类型")
    private NodeType type;

    @Schema(description = "输入参数")
    private List<NodeFieldDef> inputs;

    @Schema(description = "输出参数")
    private List<NodeFieldDef> fields;

    // 配置信息
    @Schema(description = "参数配置")
    private OptionMap options;

    // 位置布局
    @Schema(description = "页面位置布局")
    private FlowNodePosition position;

    public FlowNode(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public FlowNode title(final String title){
        setTitle(title);
        return this;
    }

    public FlowNode option(final String key, final Object value) {
        if (StrUtil.isBlank(key)) {
            return this;
        }
        if (options == null) {
            options = new OptionMap();
        }
        options.put(key, value);
        return this;
    }

    public FlowNode withOption(final Object obj){
        if(obj == null){
            return this;
        }
        final OptionMap op = new OptionMap();
        BeanUtils.copyProperties(obj, op);
        options = op;
        return this;
    }

    public FlowNode addField(final NodeFieldDef f) {
        if (f == null) {
            return this;
        }
        if (fields == null) {
            fields = new ArrayList<>();
        }
        fields.add(f);
        return this;
    }

    public FlowNode addField(final String name, final DataType dataType) {
        final NodeFieldDef f = new NodeFieldDef();
        f.setName(name);
        f.setDataType(dataType);
        return addField(f);
    }


    public FlowNode to(final NodeType nodeType, final String name) {
        final FlowNode t = new FlowNode();
        t.setId(flow.nextNodeId());
        t.setType(nodeType);
        t.setName(name);
        t.setFlow(flow);
        flow.addNode(t);
        flow.addEdge(getId(), t.getId());
        return t;
    }

    @Override
    public String toString() {
        String sb = "FlowNode{" + "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
        return sb;
    }
}
