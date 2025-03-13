package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "sys_team_user", autoResultMap = true)
@Schema(description = "系统团队用户表")
public class SysTeamUser extends SnowIdWithTimeUserEntity {
    @TableFieldIndex
    private String teamId;

    @TableFieldIndex
    private String userId;
}
