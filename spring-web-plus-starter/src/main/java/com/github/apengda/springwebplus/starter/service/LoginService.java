package com.github.apengda.springwebplus.starter.service;

import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.apengda.springwebplus.starter.pojo.LoginInfo;
import org.springframework.stereotype.Service;

/**
 * 用户登录服务
 */
@Service
public class LoginService {


    public String login(LoginInfo loginInfo) {
        // todo
        return null;
    }

    public void logout() {

    }


    /**
     * 痛殴
     *
     * @param token
     * @return
     */
    public CurrentUserInfo byToken(final String token) {
        // todo
        return null;
    }
}
