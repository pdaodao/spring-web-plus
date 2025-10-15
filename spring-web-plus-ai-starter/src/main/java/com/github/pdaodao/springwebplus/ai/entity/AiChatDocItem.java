package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "知识库文档文本块")
@TableName(value = "ai_chat_doc_item", autoResultMap = true)
public class AiChatDocItem extends SnowIdWithTimeEntity {
    @Schema(description = "文档id")
    private String docId;

    @Schema(description = "字段名称(英文)")
    @Length(max = 100, message = "字段长度超过限制")
    private String name;

    @Schema(description = "中文名称")
    @Length(max = 200, message = "中文名称长度超过限制")
    private String title;

    @Schema(description = "数据源表id")
    private String tableId;

    @Schema(description = "字段类型")
    private DataType dataType;

    @Schema(description = "在文档中的顺序")
    private Integer seq;

    @Schema(description = "数据字典id")
    private String dicId;

    @Schema(description = "内容")
    @TableFieldSize(3000)
    private String content;

    @Schema(description = "可为空")
    protected Boolean nullable;
}