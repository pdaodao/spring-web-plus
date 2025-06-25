package com.github.pdaodao.springwebplus.dao;

import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysDict;
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
public class SysDicDao extends BaseDao<SysDicMapper, SysDict> {

    public List<SysDict> listOrderBySeq(final String pid) {
        return list(QueryBuilder.lambda(SysDict.class)
                .eq(SysDict::getPid, pid)
                .build().orderByAsc(SysDict::getSeq));
    }

    public SysDict dicByName(final String name) {
        Preconditions.checkNotBlank(name, "字典编码不能为空");
        return getOne(QueryBuilder.lambda(SysDict.class)
                .eq(SysDict::getName, name)
                .eq(SysDict::getPid, "0")
                .build());
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean save(SysDict entity) {
        return super.save(entity);
    }

    @Cacheable
    public Map<String, String> dicValueMap(final String name) {
        Preconditions.checkNotBlank(name, "字典编码不能为空");
        final SysDict dic = dicByName(name);
        final Map<String, String> map = new LinkedHashMap<>();
        if(dic == null){
            return map;
        }
        for(final SysDict d : listOrderBySeq(dic.getId())){
            map.put(d.getName(), d.getTitle());
        }
        return  map;
    }
    
}
