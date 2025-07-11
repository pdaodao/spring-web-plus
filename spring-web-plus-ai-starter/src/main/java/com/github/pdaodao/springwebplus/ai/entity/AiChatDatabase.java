package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识库")
@TableName(value = "ai_chat_database", autoResultMap = true)
public class AiChatDatabase extends SnowIdWithTimeUserEntity implements WithTeam {
    @Schema(description = "标题")
    private String title;

    @Schema(description = "团队id")
    private String teamId;
}
