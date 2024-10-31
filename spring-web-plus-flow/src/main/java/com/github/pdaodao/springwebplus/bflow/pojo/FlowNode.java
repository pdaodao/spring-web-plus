package com.github.pdaodao.springwebplus.bflow.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "流程节点")
public class FlowNode {
    private String id;
    private String title;

    private String type;

    private Integer x;
    private Integer y;
}
