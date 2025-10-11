package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "大模型问答日志")
@TableName(value = "ai_chat_log", autoResultMap = true)
public class AiChatSessionLog extends SnowIdWithTimeEntity {
    @TableFieldIndex
    private String sessionId;


}
