package com.github.pdaodao.springwebplus.ai.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.*;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "问答对")
@TableName(value = "ai_question_answer", autoResultMap = true)
public class AiQuestionAnswer extends SnowIdWithTimeUserEntity implements WithTeam, WithEnabled, WithDelete,
        WithPidString, WithChildren<AiQuestionAnswer> {
    @Schema(description = "标题-问题")
    private String title;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "团队id")
    private String teamId;

    @TableFieldSize(5000)
    @Schema(description = "答案")
    private String answer;

    @Schema(description = "父分类id")
    @TableFieldSize(defaultValue = "0")
    private String pid;

    @Schema(description = "是否是分类")
    @TableFieldSize(defaultValue = "false")
    private Boolean isDir;

    @Schema(description = "是否启用")
    @TableFieldSize(defaultValue = "true")
    private Boolean enabled;

    @TableLogic
    @Schema(description = "是否删除")
    private Boolean isDeleted;

    private transient List<AiQuestionAnswer> children;

    public String content() {
        final StringBuilder sb = new StringBuilder();
        if (StrUtil.isNotBlank(title)) {
            sb.append(title);
        }
        if (StrUtil.isNotBlank(answer)) {
            sb.append(":").append(answer);
        }
        return sb.toString();
    }

    public String toEmbeddingText(){
        final StringBuilder sb = new StringBuilder();
        sb.append(title);
        return sb.toString();
    }
}