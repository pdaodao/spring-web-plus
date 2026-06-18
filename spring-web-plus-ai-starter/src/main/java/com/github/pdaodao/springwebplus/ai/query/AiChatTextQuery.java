package com.github.pdaodao.springwebplus.ai.query;

import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.base.pojo.WithTimeQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AiChatTextQuery extends WithTimeQuery {
    @Schema(description = "命名空间")
    private AiChatNamespace namespace;

    @Schema(description = "主题id")
    private String topicId;

    private String teamId;

    private String fileId;
}