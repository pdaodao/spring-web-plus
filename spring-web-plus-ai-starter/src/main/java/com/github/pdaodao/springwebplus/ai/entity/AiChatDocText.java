package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识库文档文本快")
@TableName(value = "ai_chat_doc", autoResultMap = true)
public class AiChatDocText extends SnowIdWithTimeEntity{
    @Schema(description = "文档id")
    private String docId;

    @Schema(description = "在文档中的顺序")
    private Integer seq;

    @Schema(description = "内容")
    @TableFieldSize(3000)
    private String content;
}