package com.github.pdaodao.springwebplus.dao;

import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysDict;
import com.github.pdaodao.springwebplus.mapper.SysDicMapper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
