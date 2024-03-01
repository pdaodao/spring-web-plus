package com.github.apengda.springwebplus.starter.service.impl;

import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.apengda.springwebplus.starter.service.TokenStore;

public class RedisTokenStore implements TokenStore {
    @Override
    public String buildToken(CurrentUserInfo userInfo) {
        return null;
    }

    @Override
    public CurrentUserInfo byToken(String token) {
        return null;
    }

    @Override
    public void removeToken(String token) {

    }

    @Override
    public void storeToken(String token, CurrentUserInfo userInfo) {

    }
}
