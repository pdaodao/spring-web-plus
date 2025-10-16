package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "知识文档小类")
public enum ChatDocType {
    @Schema(description = "字段")
    field,
    @Schema(description = "文本")
    text
}