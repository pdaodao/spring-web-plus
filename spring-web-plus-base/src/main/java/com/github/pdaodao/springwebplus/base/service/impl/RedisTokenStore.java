package com.github.pdaodao.springwebplus.base.service.impl;

import com.github.pdaodao.springwebplus.base.config.SysConfigProperties;
import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.service.TokenStore;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisTokenStore implements TokenStore {

    public RedisTokenStore(SysConfigProperties sysConfig, RedisTemplate redisTemplate) {

    }

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
