package com.github.pdaodao.springwebplus.tool.flow.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "连线")
public class FlowEdge {
    @Schema(description = "连线id")
    private String id;

    @Schema(description = "来源节点id")
    private String source;

    @Schema(description = "目标节点id")
    private String target;

    @Schema(description = "来源端口")
    private Integer sourcePort;

    @Schema(description = "目标端口")
    private Integer targetPort;
}
