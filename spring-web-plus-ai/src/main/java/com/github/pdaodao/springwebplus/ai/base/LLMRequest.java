package com.github.pdaodao.springwebplus.ai.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "大模型问答系统请求数据")
public class LLMRequest {
    @Schema(description = "场景id")
    private String appId;

    @Schema(description = "问答类型,ChatBi、...")
    private String chatType;

    @Schema(description = "场景的阶段如生成sql，查询数据, 归因分析，预测分析等")
    private String phase;

    @Schema(description = "会话id")
    private String sessionId;

    @Schema(description = "问答消息id")
    private String msgId;

    @Schema(description = "问题")
    private String question;

    @Schema(description = "知识库文档id")
    private List<String> docIds;

    @Schema(description = "会话文档id")
    private List<String> sessionDocIds;
}