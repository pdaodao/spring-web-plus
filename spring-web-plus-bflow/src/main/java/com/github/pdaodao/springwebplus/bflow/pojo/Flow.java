package com.github.pdaodao.springwebplus.bflow.pojo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "流程节点定义")
public class Flow {
    private List<FlowNode> nodes;
    private List<FlowEdge> edges;
}
