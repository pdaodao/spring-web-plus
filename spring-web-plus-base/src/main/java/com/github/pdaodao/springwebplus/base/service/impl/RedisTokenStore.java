package com.github.pdaodao.springwebplus.base.service.impl;

import com.github.pdaodao.springwebplus.base.config.SysConfigProperties;
import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;
import com.github.pdaodao.springwebplus.base.service.TokenStore;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisTokenStore implements TokenStore {
    private final SysConfigProperties config;
    private final RedisTemplate redisTemplate;

    public RedisTokenStore(SysConfigProperties sysConfig, RedisTemplate redisTemplate) {
        this.config = sysConfig;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public TokenInfo byToken(String token) {
        return null;
    }

    @Override
    public void removeToken(String token) {

    }

    @Override
    public void storeToken(String token, TokenInfo tokenInfo) {

    }
}
