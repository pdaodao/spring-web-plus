package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithDelete;
import com.github.pdaodao.springwebplus.base.entity.WithEnabled;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识库文档")
@TableName(value = "ai_chat_doc", autoResultMap = true)
public class AiChatDoc extends SnowIdWithTimeUserEntity implements WithTeam, WithEnabled, WithDelete {
    @Schema(description = "知识库id")
    private String dbId;

    @Schema(description = "标题-文件名称")
    private String title;

    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "文件id")
    private String fileId;

    @Schema(description = "字符数")
    private Integer charCount;

    @Schema(description = "文本块数")
    private Integer textSize;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "是否嵌入完成")
    private Boolean isEmbed;

    @TableLogic
    private Boolean isDeleted;
}
