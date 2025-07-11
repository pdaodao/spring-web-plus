package com.github.pdaodao.springwebplus.dao;

import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysDic;
import com.github.pdaodao.springwebplus.mapper.SysDicMapper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@CacheConfig(cacheNames = "sysDic")
public class SysDicDao extends BaseDao<SysDicMapper, SysDic> {

    public List<SysDic> listOrderBySeq(final String pid) {
        return list(QueryBuilder.lambda(SysDic.class)
                .eq(SysDic::getPid, pid)
                .build().orderByAsc(SysDic::getSeq));
    }

    public SysDic dicByName(final String name) {
        Preconditions.checkNotBlank(name, "字典编码不能为空");
        return getOne(QueryBuilder.lambda(SysDic.class)
                .eq(SysDic::getName, name)
                .eq(SysDic::getPid, "0")
                .build());
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean save(SysDic entity) {
        return super.save(entity);
    }

    @Cacheable
    public Map<String, String> dicValueMap(final String name) {
        Preconditions.checkNotBlank(name, "字典编码不能为空");
        final SysDic dic = dicByName(name);
        final Map<String, String> map = new LinkedHashMap<>();
        if(dic == null){
            return map;
        }
        for(final SysDic d : listOrderBySeq(dic.getId())){
            map.put(d.getName(), d.getTitle());
        }
        return  map;
    }
    
}
