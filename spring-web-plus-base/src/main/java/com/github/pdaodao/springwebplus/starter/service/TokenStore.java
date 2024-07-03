package com.github.pdaodao.springwebplus.starter.service;

import com.github.pdaodao.springwebplus.starter.pojo.CurrentUserInfo;

public interface TokenStore {
    String buildToken(final CurrentUserInfo userInfo);

    CurrentUserInfo byToken(final String token);

    void removeToken(final String token);

    void storeToken(final String token, CurrentUserInfo userInfo);
}
