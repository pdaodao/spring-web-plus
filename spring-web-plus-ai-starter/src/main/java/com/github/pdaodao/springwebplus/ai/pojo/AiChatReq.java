package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问答请求")
public class AiChatReq {
    @Schema(description = "问答场景id")
    private String chatAppId;

    @Schema(description = "会话id")
    private String sessionId;
}
