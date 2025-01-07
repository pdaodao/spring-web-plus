package com.github.pdaodao.springwebplus.base.service;

import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;

public interface TokenStore {
    TokenInfo byToken(final String token);

    void removeToken(final String token);

    void storeToken(final String token, TokenInfo tokenInfo);
}
