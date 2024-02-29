package com.github.apengda.springwebplus.starter.service;

import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.apengda.springwebplus.starter.pojo.LoginInfo;
import org.springframework.stereotype.Service;

/**
 * 用户登录服务
 */
@Service
public interface LoginService {

    /**
     * 登录返回token
     * @param loginInfo
     * @return
     */
    String login(LoginInfo loginInfo);

    /**
     * 退出登录
     */
    void logout();


    /**
     * 通过token获取用户信息
     *
     * @param token
     * @return
     */
    CurrentUserInfo byToken(final String token);
}
