package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "知识文档小类")
public enum ChatDocType {
    @Schema(description = "数据表")
    table,
    @Schema(description = "字段")
    field,
    @Schema(description = "pdf")
    pdf,
    @Schema(description = "demoValue")
    demoValue
}