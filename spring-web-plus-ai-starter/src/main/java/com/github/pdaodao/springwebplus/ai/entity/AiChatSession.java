package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "大模型问答会话")
@TableName(value = "zt_chat_session", autoResultMap = true)
public class AiChatSession extends SnowIdWithTimeUserEntity implements WithTeam {
    @Schema(description = "命名空间 chat,table,api")
    private String namespace;

    @TableFieldIndex
    private String objId;

    @Schema(description = "会话标题")
    private String title;

    private String teamId;
}
