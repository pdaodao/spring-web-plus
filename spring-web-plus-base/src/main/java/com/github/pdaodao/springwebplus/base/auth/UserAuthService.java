package com.github.pdaodao.springwebplus.base.auth;

import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;

import java.util.Set;

public interface UserAuthService {
    /**
     * 用户信息
     * @param userId
     * @return
     */
    CurrentUserInfo userInfo(final String userId);

    /**
     * 用户角色编码
     * @param userId
     * @return
     */
     String userRole(final String userId);

    /**
     * 用户权限列表
     * @param userId
     * @return
     */
     Set<String> userPermission(final String userId);
}
