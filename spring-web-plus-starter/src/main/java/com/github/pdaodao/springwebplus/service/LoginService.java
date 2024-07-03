package com.github.pdaodao.springwebplus.service;

import com.github.pdaodao.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.starter.pojo.LoginInfo;
import org.springframework.stereotype.Service;

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
    CurrentUserInfo login(final LoginInfo loginInfo);

    /*
     * 退出登录
     */
    void logout(CurrentUserInfo userInfo);
}
