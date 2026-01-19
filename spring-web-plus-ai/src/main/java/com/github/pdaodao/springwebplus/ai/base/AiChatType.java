package com.github.pdaodao.springwebplus.ai.base;

import io.swagger.v3.oas.annotations.media.Schema;

public enum AiChatType {
    @Schema(description = "文本问答")
    TextGen,
    @Schema(description = "数据问答")
    ChatBi,
    @Schema(description = "接口管理")
    ApiGen,
    @Schema(description = "数据表管理")
    TableGen,
    @Schema(description = "页面设计")
    PageGen,
    @Schema(description = "数据运维管理")
    DataTaskGen
}