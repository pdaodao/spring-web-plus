package com.github.pdaodao.springwebplus.tool.ui;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UiCheckRule {
    @Schema(description = "校验类型 notblank、email类型、不重复、reg正则定义等")
    private String type;

    @Schema(description = "规则内容")
    private String content;
}