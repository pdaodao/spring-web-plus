package com.github.apengda.springwebplus.service;

import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.apengda.springwebplus.starter.pojo.LoginInfo;
import com.github.apengda.springwebplus.starter.service.LoginService;
import org.springframework.stereotype.Service;

@Service
public class SysUserLoginService implements LoginService {
    @Override
    public String login(LoginInfo loginInfo) {
        return null;
    }

    @Override
    public void logout() {

    }

    @Override
    public CurrentUserInfo byToken(String token) {
        return null;
    }
}
