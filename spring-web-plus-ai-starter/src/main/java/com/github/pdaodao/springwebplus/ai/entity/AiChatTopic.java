package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithPidString;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import java.util.List;

@Data
@Schema(description = "知识主题")
@TableName(value = "ai_chat_topic", autoResultMap = true)
public class AiChatTopic extends SnowIdWithTimeUserEntity implements WithTeam, WithPidString {
    @Schema(description = "命名空间")
    private AiChatNamespace namespace;

    @Schema(description = "标题-问题")
    private String title;

    @Schema(description = "团队id")
    @TableFieldSize(defaultValue = "0")
    private String teamId;

    @Schema(description = "描述")
    private String remark;

    @TableFieldSize(defaultValue = "0")
    private String pid;

    @Schema(description = "父主题分类名称")
    private transient String topicTitle;

    @Schema(description = "文件类型")
    @TableFieldSize(defaultValue = "topic")
    private String fileType;

    @Schema(description = "存储路径")
    @Length(max = 800, message = "存储路径长度超过800限制")
    private String filePath;

    private transient List<AiChatTopic> children;
}