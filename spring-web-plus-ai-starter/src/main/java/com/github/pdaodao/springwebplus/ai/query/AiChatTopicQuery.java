package com.github.pdaodao.springwebplus.ai.query;

import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AiChatTopicQuery extends PageRequestParam {
    @Schema(description = "命名空间")
    private AiChatNamespace namespace;

    private String pid = "0";

    private String teamId;
}
