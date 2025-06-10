package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.pojo.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@TableName(value = "sys_team_user", autoResultMap = true)
@Schema(description = "系统团队用户表")
public class SysTeamUser extends AutoIdWithTimeUserEntity {
    @TableFieldIndex
    @NotBlank(message = "团队id不能为空")
    private Long teamId;

    @TableFieldIndex
    @NotBlank(message = "用户id不能为空")
    private Long userId;

    private MemberType memberType;

    private transient String username;

    private transient String userNickname;
}
