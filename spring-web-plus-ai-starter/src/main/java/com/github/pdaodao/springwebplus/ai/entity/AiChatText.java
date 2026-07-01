package com.github.pdaodao.springwebplus.ai.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.base.entity.*;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "问答知识文本")
@TableName(value = "ai_chat_text", autoResultMap = true)
public class AiChatText extends SnowIdWithTimeUserEntity implements WithTeam{
    @Schema(description = "命名空间")
    private AiChatNamespace namespace;

    @Schema(description = "标题-问题")
    private String title;

    @TableFieldSize(5000)
    @Schema(description = "答案-内容")
    private String content;

    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "主题id")
    @TableFieldSize(defaultValue = "0")
    private String topicId;

    @Schema(description = "主题标题")
    private transient String topicTitle;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "文件id")
    private String fileId;

    @Schema(description = "在文档中的顺序")
    private Integer seq;

    private transient List<AiChatText> children;

    public static AiChatText of(final String teamId, final AiChatNamespace namespace, final String id){
        final AiChatText text = new AiChatText();
        text.setTeamId(teamId);
        text.setNamespace(namespace);
        text.setId(id);
        return text;
    }

    public String content() {
        final StringBuilder sb = new StringBuilder();
        if (StrUtil.isNotBlank(title)) {
            sb.append(title);
        }
        if (StrUtil.isNotBlank(content)) {
            if(sb.length() > 1){
                sb.append("\n");
            }
            sb.append(content);
        }
        return sb.toString();
    }

    public String toEmbeddingText(){
        return content();
    }
}