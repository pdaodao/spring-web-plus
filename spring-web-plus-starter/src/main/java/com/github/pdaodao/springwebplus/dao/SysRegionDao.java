package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.entity.SysRegion;
import com.github.pdaodao.springwebplus.mapper.SysRegionMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@CacheConfig(cacheNames = "SysRegion")
public class SysRegionDao extends BaseDao<SysRegionMapper, SysRegion> {

    @Cacheable
    public List<SysRegion> all(){
        return list();
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean save(SysRegion entity) {
        return super.save(entity);
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAll(final List<SysRegion> regionList) {
        if(CollUtil.isEmpty(regionList)){
            return false;
        }
        for(final SysRegion r: regionList){
            if(StrUtil.isEmpty(r.getId())){
                continue;
            }
            save(r);
        }
        return true;
    }
}
