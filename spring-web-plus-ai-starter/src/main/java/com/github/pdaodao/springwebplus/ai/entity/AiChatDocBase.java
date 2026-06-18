package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.github.pdaodao.springwebplus.base.entity.*;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AiChatDocBase extends SnowIdWithTimeUserEntity implements WithTeam, WithEnabled, WithDelete,
        WithPidString, WithChildren<AiChatDocBase>{
    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "团队id")
    private String teamId;

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

    private transient List<AiChatDocBase> children;
}