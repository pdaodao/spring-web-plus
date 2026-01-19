package com.github.pdaodao.springwebplus.ai.base;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 问答知识命名空间 相当于是数据表名
 */
public enum AiChatNamespace {
    @Schema(description = "数据源")
    database,
    @Schema(description = "接口")
    api,
    @Schema(description = "术语")
    term,
    @Schema(description = "知识库")
    knowledge
}
