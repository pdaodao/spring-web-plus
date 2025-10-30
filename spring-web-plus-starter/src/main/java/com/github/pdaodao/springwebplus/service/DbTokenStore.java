package com.github.pdaodao.springwebplus.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;
import com.github.pdaodao.springwebplus.base.service.TokenStore;
import com.github.pdaodao.springwebplus.dao.SysUserTokenDao;
import com.github.pdaodao.springwebplus.entity.SysUserTokenEntity;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

@CacheConfig(cacheNames = "sysUserToken")
@AllArgsConstructor
public class DbTokenStore implements TokenStore {
    private final SysUserTokenDao tokenDao;

    @Cacheable
    @Override
    public TokenInfo byToken(String token) throws Exception {
        if(StrUtil.isBlank(token)){
            return null;
        }
        final SysUserTokenEntity entity = tokenDao.getById(token);
        if(entity == null){
            return null;
        }
        return entity.toTokenInfo();
    }

    @Override
    public void removeToken(String token) throws Exception {
        if(StrUtil.isBlank(token)){
            return;
        }
        tokenDao.removeById(token);
    }

    @Override
    public void storeToken(String token, TokenInfo tokenInfo) throws Exception {
        final SysUserTokenEntity entity = new SysUserTokenEntity();
        BeanUtil.copyProperties(tokenInfo, entity);
        entity.setId(token);
        tokenDao.save(entity);
    }
}
