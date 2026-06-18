package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysApiKey;
import com.github.pdaodao.springwebplus.mapper.SysApiKeyMapper;
import com.github.pdaodao.springwebplus.util.Constant;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.List;


@Component
@CacheConfig(cacheNames = "sysApiKey")
public class SysApiKeyDao extends BaseDao<SysApiKeyMapper, SysApiKey> {
    public List<SysApiKey> infoList(final String teamId){
        final List<SysApiKey> list =  list(QueryBuilder.lambda(SysApiKey.class)
                .eq(SysApiKey::getTeamId, teamId)
                .build());
        for(final SysApiKey k: list){
            k.setApiKey(Constant.FakePassword);
        }
        return list;
    }

    @Override
    public boolean save(SysApiKey entity) {
        if(StrUtil.equals(Constant.FakePassword, entity.getApiKey())){
            entity.setApiKey(null);
        }
        return super.save(entity);
    }

    @Cacheable(key = "#p0", condition = "#p0 != null")
    public SysApiKey getByApiKey(final String apiKey) {
        return getOne(QueryBuilder.lambda(SysApiKey.class)
                .eq(SysApiKey::getApiKey, apiKey)
                .build());
    }


    @CacheEvict(key = "#p0", condition = "#p0 != null")
    public void clearCache(final String apiKey){
    }
}
