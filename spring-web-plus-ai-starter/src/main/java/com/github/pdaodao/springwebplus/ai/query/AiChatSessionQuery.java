package com.github.pdaodao.springwebplus.ai.query;

import com.github.pdaodao.springwebplus.base.pojo.WithTimeQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AiChatSessionQuery extends WithTimeQuery {
    @Schema(description = "问答场景id")
    private String chatAppId;

    @Schema(hidden = true)
    private String userId;

    // 对象id 如 接口id,
    private String objId;

    @Schema(hidden = true)
    private String teamId;
}
