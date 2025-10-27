package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysDic;
import com.github.pdaodao.springwebplus.mapper.SysDicMapper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@CacheConfig(cacheNames = "sysDic")
public class SysDicDao extends BaseDao<SysDicMapper, SysDic> {
    public List<SysDic> list(final String pid,  final String title){
        return list(QueryBuilder.lambda(SysDic.class)
                .eq(SysDic::getPid, pid)
                .like(title, SysDic::getName, SysDic::getTitle).build());
    }

    @Override
    @CacheEvict(key = "#p0.id", condition = "#p0.id != null")
    public boolean save(SysDic entity) {
        return super.save(entity);
    }

    @Cacheable(key = "all")
    public List<SysDic> allList(){
        return list();
    }

    @CacheEvict(key = "all")
    public void  allListClear(){
    }


    @CacheEvict(key = "#p0", condition = "#p0 != null")
    public void clearValue(final String pid){
    }
    @CacheEvict(key = "#p0", condition = "#p0 != null")
    public void saveValues(final String pid, final List<SysDic> values){
        Preconditions.checkNotBlank(pid, "pid is empty");
        if(CollUtil.isEmpty(values)){
            return;
        }
        remove(QueryBuilder.lambda(SysDic.class).eq(SysDic::getPid, pid).build());
        for(final SysDic v: values){
            v.setPid(pid);
        }
        saveBatch(values);
    }

    @Cacheable(key = "#p0", condition = "#p0 != null")
    public List<SysDic> values(final String pid){
        return list(QueryBuilder.lambda(SysDic.class).eq(SysDic::getPid, pid).build());
    }
}
