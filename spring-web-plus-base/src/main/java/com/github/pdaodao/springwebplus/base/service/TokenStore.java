package com.github.pdaodao.springwebplus.base.service;

import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;

public interface TokenStore {
    String buildToken(final CurrentUserInfo userInfo);

    CurrentUserInfo byToken(final String token);

    void removeToken(final String token);

    void storeToken(final String token, CurrentUserInfo userInfo);
}
