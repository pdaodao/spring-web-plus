package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithDelete;
import com.github.pdaodao.springwebplus.base.entity.WithEnabled;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AiSkill extends SnowIdWithTimeUserEntity implements WithTeam, WithEnabled, WithDelete {

    @TableFieldIndex
    @TableFieldSize(defaultValue = "0")
    private String teamId;

    @Schema(description = "是否启用")
    @TableFieldSize(defaultValue = "1")
    private Boolean enabled;

    @TableLogic
    @TableFieldSize(defaultValue = "0")
    private Boolean isDeleted;
}