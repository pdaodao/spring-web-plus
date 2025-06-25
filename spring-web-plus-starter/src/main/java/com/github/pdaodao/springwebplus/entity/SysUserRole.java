package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "sys_user_role", autoResultMap = true)
public class SysUserRole extends BaseEntity {
    /**
     * 用户id
     */
    @TableFieldIndex
    private String userId;

    /**
     * 角色id
     */
    @TableFieldIndex
    private String roleId;

    @Schema(description = "团队id")
    private String teamId;
}
