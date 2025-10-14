package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

public enum AiChatType {
    @Schema(description = "大模型问答")
    llm,
    @Schema(description = "数据库sql问答")
    sql
}
