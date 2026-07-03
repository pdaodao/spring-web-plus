package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问答场景主题关联表")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName(value = "ai_chat_app_topic", autoResultMap = true)
public class AiChatAppTopic extends SnowIdWithTimeEntity {
    @TableFieldIndex
    @Schema(description = "应用id")
    private String appId;

    @TableFieldIndex
    @Schema(description = "主题id")
    private String topicId;

    @Schema(description = "主题标题")
    private transient String topicTitle;

    @Schema(description = "主题命名空间")
    private transient AiChatNamespace topicNamespace;
}