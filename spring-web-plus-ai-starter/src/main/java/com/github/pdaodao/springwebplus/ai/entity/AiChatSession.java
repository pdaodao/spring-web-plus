package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithDelete;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "大模型问答会话")
@TableName(value = "ai_chat_session", autoResultMap = true)
public class AiChatSession extends SnowIdWithTimeUserEntity implements WithTeam, WithDelete {
    @Schema(description = "命名空间 chat,table,api")
    private String namespace;

    // 如数据源id, 应用id
    @Schema(description = "业务主体id")
    private String objId;

    @Schema(description = "会话标题")
    private String title;

    @Schema(description = "团队id")
    private String teamId;

    @TableLogic
    private Boolean isDeleted;
}