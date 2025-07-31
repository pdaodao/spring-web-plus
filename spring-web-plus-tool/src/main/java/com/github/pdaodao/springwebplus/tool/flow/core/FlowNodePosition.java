package com.github.pdaodao.springwebplus.tool.flow.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "页面位置布局")
public class FlowNodePosition {
    @Schema(description = "形状")
    private String shape = "rect";

    @Schema(description = "x")
    private Integer x;

    @Schema(description = "y")
    private Integer y;

    @Schema(description = "宽度")
    private Integer with;

    @Schema(description = "高度")
    private Integer height;
}
