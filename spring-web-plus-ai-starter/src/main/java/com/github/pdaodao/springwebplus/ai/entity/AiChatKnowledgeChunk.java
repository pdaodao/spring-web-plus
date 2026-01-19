package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "知识库文本块")
@TableName(value = "ai_chat_knowledge_chunk", autoResultMap = true)
public class AiChatKnowledgeChunk extends SnowIdWithTimeEntity {
    @TableFieldIndex
    @Schema(description = "知识库文档id")
    private String knowledgeId;

    @Schema(description = "中文名称")
    @Length(max = 200, message = "中文名称长度超过限制")
    private String title;

    @Schema(description = "在文档中的顺序")
    private Integer seq;

    @Schema(description = "内容")
    @TableFieldSize(3000)
    private String content;
}