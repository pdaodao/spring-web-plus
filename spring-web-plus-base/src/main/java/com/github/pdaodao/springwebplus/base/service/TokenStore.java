package com.github.pdaodao.springwebplus.base.service;

import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;
import java.util.List;

public interface TokenStore {
    TokenInfo byToken(final String token) throws Exception;

    default List<TokenInfo> byPrefix(final String prefix) throws Exception{
        return null;
    }

    void removeToken(final String token) throws Exception;

    void storeToken(final String token, TokenInfo tokenInfo) throws Exception;
}
