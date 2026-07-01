package com.github.pdaodao.springwebplus.service;

import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.LoginUserInfo;
import com.github.pdaodao.springwebplus.entity.SysRole;
import com.github.pdaodao.springwebplus.pojo.UserPassword;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户登录服务
 */
@Service
public interface LoginService {
    /**
     * 登录
     *
     * @param loginInfo
     * @return
     */
    CurrentUserInfo login(final LoginUserInfo loginInfo);

    /**
     * 修改密码
     * @param userPassword
     * @return
     */
    Boolean updatePassword(final UserPassword userPassword);


    /*
     * 退出登录
     */
    void logout(CurrentUserInfo userInfo);

    List<SysRole> userRoles(final String userId);
}
