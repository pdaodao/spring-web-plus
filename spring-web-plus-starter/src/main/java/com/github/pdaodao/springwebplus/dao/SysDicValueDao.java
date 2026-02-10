package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysDicValue;
import com.github.pdaodao.springwebplus.mapper.SysDicValueMapper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@CacheConfig(cacheNames = "sysDicValue")
public class SysDicValueDao extends BaseDao<SysDicValueMapper, SysDicValue> {
    @Cacheable(key = "#p0")
    public List<SysDicValue> values(final String dicId){
        return list(QueryBuilder.lambda(SysDicValue.class)
                .eq(SysDicValue::getDicId, dicId).build());
    }

    public List<SysDicValue> valuePage(final String dicId, final String q){
        return list(QueryBuilder.lambda(SysDicValue.class)
                .like(q, SysDicValue::getName, SysDicValue::getTitle)
                .eq(SysDicValue::getDicId, dicId).build());
    }

    @CacheEvict(allEntries = true)
    public void cacheClear(){
    }

    private SysDicValue byName(final String dicId, final String name){
        return getOne(QueryBuilder.lambda(SysDicValue.class)
                .eq(SysDicValue::getDicId, dicId)
                .eq(SysDicValue::getName, name).build());
    }

    private SysDicValue byTitle(final String dicId, final String title){
        return getOne(QueryBuilder.lambda(SysDicValue.class)
                .eq(SysDicValue::getDicId, dicId)
                .eq(SysDicValue::getName, title).build());
    }

    @Override
    protected void saveCheck(SysDicValue entity, boolean isInsert) {
        SysDicValue old = byName(entity.getDicId(), entity.getName());
        if (old != null) {
            Preconditions.checkArgument(ObjectUtil.equals(entity.getId(), old.getId()), entity.getName()+" 编码已存在");
        }
        old = byTitle(entity.getDicId(), entity.getTitle());
        if (old != null) {
            Preconditions.checkArgument(ObjectUtil.equals(entity.getId(), old.getId()), entity.getTitle()+" 显示名称已存在");
        }
    }
}