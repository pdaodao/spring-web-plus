package com.github.pdaodao.springwebplus.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.pdaodao.springwebplus.base.config.SysConfigProperties;
import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;
import com.github.pdaodao.springwebplus.base.service.TokenStore;
import java.time.Duration;

public class LocalTokenStore implements TokenStore {
    private final SysConfigProperties sysConfig;
    private final Cache<String, TokenInfo> map;

    public LocalTokenStore(SysConfigProperties sysConfig) {
        this.sysConfig = sysConfig;
        map = Caffeine.newBuilder()
                .maximumSize(20000)
                .expireAfterAccess(Duration.ofMinutes(sysConfig.getAuthExpire()))
                .build();
    }


    @Override
    public TokenInfo byToken(final String token) {
        return map.getIfPresent(token);
    }

    @Override
    public void removeToken(final String token) {
        map.invalidate(token);
    }

    @Override
    public void storeToken(String token, TokenInfo userInfo) {
        if (StrUtil.isBlank(token) || userInfo == null) {
            return;
        }
        map.put(token, userInfo);
    }
}
