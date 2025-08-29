package com.github.pdaodao.flow.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "任务参与类型")
public enum TaskPerformType {
    @Schema(description = "普通参与")
    normal,

    @Schema(description = "会签参与")
    countersign;
}
