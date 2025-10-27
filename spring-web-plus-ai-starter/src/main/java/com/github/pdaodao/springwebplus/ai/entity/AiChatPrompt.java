package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "提示词")
@TableName(value = "ai_chat_prompt", autoResultMap = true)
public class AiChatPrompt extends SnowIdEntity {
    @Schema(description = "内容")
    @TableFieldSize(3000)
    private String content;
}
