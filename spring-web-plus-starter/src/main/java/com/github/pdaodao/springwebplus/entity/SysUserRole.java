package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import lombok.Data;

@Data
@TableName(value = "sys_user_role", autoResultMap = true)
public class SysUserRole extends SnowIdWithTimeUserEntity {
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
}
