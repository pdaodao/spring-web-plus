package com.github.pdaodao.springwebplus.dao;

import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysOauth2Client;
import com.github.pdaodao.springwebplus.mapper.SysOauth2ClientMapper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = "SysOauth2Client")
public class SysOauth2ClientDao extends BaseDao<SysOauth2ClientMapper, SysOauth2Client> {

    @Cacheable
    public SysOauth2Client byClientId(final String clientId) {
        Preconditions.checkNotBlank(clientId, "clientId is blank.");
        return getOne(QueryBuilder.lambda(SysOauth2Client.class)
                .eq(SysOauth2Client::getClientId, clientId).build());
    }
}
