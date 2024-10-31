package com.github.pdaodao.springwebplus.service;

import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.LoginUserInfo;
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
    CurrentUserInfo login(final LoginUserInfo loginInfo);

    /*
     * 退出登录
     */
    void logout(CurrentUserInfo userInfo);
}
