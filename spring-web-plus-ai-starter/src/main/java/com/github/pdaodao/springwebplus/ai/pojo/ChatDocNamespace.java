package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "知识文档大类")
public enum ChatDocNamespace {
    @Schema(description = "文件")
    file,
    @Schema(description = "文本文档")
    text,
    @Schema(description = "数据表")
    table,
    @Schema(description = "sql语句")
    sql,
    @Schema(description = "excel文件")
    excel,
    @Schema(description = "demoValue")
    demoValue
}