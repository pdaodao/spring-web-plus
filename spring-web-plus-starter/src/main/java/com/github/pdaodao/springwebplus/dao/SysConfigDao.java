package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysConfig;
import com.github.pdaodao.springwebplus.mapper.SysConfigMapper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@CacheConfig(cacheNames = "SysConfig")
public class SysConfigDao extends BaseDao<SysConfigMapper, SysConfig> {


    @Cacheable
    public List<SysConfig> all(){
        return list();
    }

    @CacheEvict(allEntries = true)
    public void clearCache(){
    }

    public SysConfig byKey(final String key){
        return getOne(QueryBuilder.lambda(SysConfig.class)
                .eq(SysConfig::getConfigKey, key).build());
    }

    @Override
    protected void saveCheck(SysConfig entity, boolean isInsert) {
        final SysConfig old = byKey(entity.getConfigKey());
        if (old != null) {
            Preconditions.checkArgument(ObjectUtil.equals(entity.getId(), old.getId()), "key已存在");
        }
    }
}
