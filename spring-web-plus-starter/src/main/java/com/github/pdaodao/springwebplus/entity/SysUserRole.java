package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import lombok.Data;

@Data
@TableName(value = "sys_user_role", autoResultMap = true)
public class SysUserRole extends AutoIdWithTimeUserEntity {
    /**
     * 用户id
     */
    @TableFieldIndex
    private Long userId;

    /**
     * 角色id
     */
    @TableFieldIndex
    private Long roleId;
}
