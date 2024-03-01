package com.github.apengda.springwebplus.service;

import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.apengda.springwebplus.starter.pojo.LoginInfo;
import org.springframework.stereotype.Service;

@Service
public class SysUserLoginService implements LoginService {

    @Override
    public CurrentUserInfo login(LoginInfo loginInfo) {
        return null;
    }

    @Override
    public void logout(CurrentUserInfo userInfo) {

    }
}
