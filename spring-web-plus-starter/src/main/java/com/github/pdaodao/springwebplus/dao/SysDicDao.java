package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysDic;
import com.github.pdaodao.springwebplus.entity.SysDicValue;
import com.github.pdaodao.springwebplus.mapper.SysDicMapper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.List;

@Component
@CacheConfig(cacheNames = "sysDic")
public class SysDicDao extends BaseDao<SysDicMapper, SysDic> {
    @Autowired
    private SysDicValueDao valueDao;

    @Cacheable
    public List<SysDic> infoList(){
        return list();
    }

    public List<SysDic> byPid(final String id){
        return list(QueryBuilder.lambda(SysDic.class).eq(SysDic::getPid, id).build());
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean save(SysDic entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    public List<SysDicValue> values(final String dicId){
        return valueDao.values(dicId);
    }

    public List<SysDicValue> valuePage(String dicId, String q) {
        return valueDao.valuePage(dicId, q);
    }

    public Boolean valueDelete(List<String> list) {
        valueDao.removeByIds(list);
        valueDao.cacheClear();
        return true;
    }

    public SysDicValue valueSave(SysDicValue entity) {
        Preconditions.checkNotBlank(entity.getDicId(), "dicId is null.");
        valueDao.save(entity);
        valueDao.cacheClear();
        return entity;
    }

    public Boolean valuesSave(List<SysDicValue> list) {
        if(CollUtil.isEmpty(list)){
            return false;
        }
        final String dicId = list.get(0).getDicId();
        Preconditions.checkNotBlank(dicId, "dicId is null.");
        valueDao.cacheClear();
        valueDao.saveOrUpdateBatch(list);
        return true;
    }
}