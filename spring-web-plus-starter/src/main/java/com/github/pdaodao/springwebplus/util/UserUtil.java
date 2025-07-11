package com.github.pdaodao.springwebplus.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.entity.SysRole;

import java.util.List;

public class UserUtil {
    /**
     * 是否是超级管理员角色
     * @param roleId
     * @return
     */
    public static boolean isRoleRoot(final String roleId){
        return StrUtil.equals(roleId, "1");
    }

    public static boolean isRoleRoot(final List<String> roleIds){
        if(CollUtil.isEmpty(roleIds)){
            return false;
        }
        for(final String r: roleIds){
            final boolean is = isRoleRoot(r);
            if(is){
                return true;
            }
        }
        return false;
    }

    public static boolean isRoleInfoRoot(final List<SysRole> roles){
        if(CollUtil.isEmpty(roles)){
            return false;
        }
        for(final SysRole r: roles){
            final boolean is = isRoleRoot(r.getId());
            if(is){
                return true;
            }
        }
        return false;
    }
}
