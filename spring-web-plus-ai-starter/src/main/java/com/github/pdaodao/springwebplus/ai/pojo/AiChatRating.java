package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

public enum AiChatRating {
    @Schema(description = "未评价")
    none,
    @Schema(description = "点赞")
    good,
    @Schema(description = "点踩")
    bad
}
