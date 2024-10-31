package com.github.pdaodao.springwebplus.bflow.pojo;

import lombok.Data;

@Data
public class FlowEdge {
    private String id;
    private String type;
    private String sourceNodeId;
    private String targetNodeId;
}
