package com.github.pdaodao.springwebplus.ai.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.*;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import java.util.List;

@Data
@Schema(description = "知识库")
@TableName(value = "ai_chat_knowledge", autoResultMap = true)
public class AiChatKnowledge extends SnowIdWithTimeUserEntity implements WithTeam, WithEnabled, WithDelete,
        WithPidString, WithChildren<AiChatKnowledge> {
    @Schema(description = "标题-文件名称")
    private String title;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "文件类型")
    private String type;


    @Schema(description = "文本块-字段列表")
    @TableField(exist = false)
    private List<AiChatKnowledgeChunk> chunkList;

    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "存储路径")
    @Length(max = 800, message = "存储路径长度超过800限制")
    private String filePath;

    @Schema(description = "文本块数")
    private Long chunkCount;

    @Schema(description = "字符数")
    private Long charCount;

    @Schema(description = "父分类id")
    @TableFieldSize(defaultValue = "0")
    private String pid;

    @Schema(description = "是否是分类")
    @TableFieldSize(defaultValue = "false")
    private Boolean isDir;

    @Schema(description = "是否启用")
    @TableFieldSize(defaultValue = "true")
    private Boolean enabled;

    @Schema(description = "是否嵌入完成")
    private Boolean isEmbed;

    @TableLogic
    private Boolean isDeleted;

    private transient List<AiChatKnowledge> children;

    public String content() {
        final StringBuilder sb = new StringBuilder();
        if (StrUtil.isNotBlank(title)) {
            sb.append(title);
        }
        if (StrUtil.isNotBlank(remark)) {
            sb.append(":").append(remark);
        }
        return sb.toString();
    }
}