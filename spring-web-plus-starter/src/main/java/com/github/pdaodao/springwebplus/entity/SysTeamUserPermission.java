package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "sys_team_user_permission", autoResultMap = true)
@Schema(description = "系统团队用户权限表")
public class SysTeamUserPermission extends SnowIdWithTimeUserEntity {
    @TableFieldIndex
    @Schema(description = "team_user表的id")
    private String teamUserId;

    @TableFieldIndex
    @Schema(description = "命名空间")
    private String namespace;

    @TableFieldIndex
    @Schema(description = "对象id")
    private String objId;

    @Schema(description = "授权操作")
    private String operate;
}
