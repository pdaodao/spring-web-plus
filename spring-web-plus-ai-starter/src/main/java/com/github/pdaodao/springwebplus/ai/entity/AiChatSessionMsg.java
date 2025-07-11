package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "大模型问答会话消息")
@TableName(value = "ai_chat_session_msg", autoResultMap = true)
public class AiChatSessionMsg extends SnowIdWithTimeEntity {
    @TableFieldIndex
    @Schema(description = "会话id")
    private String sessionId;

    @Schema(description = "内容")
    private String content;
}