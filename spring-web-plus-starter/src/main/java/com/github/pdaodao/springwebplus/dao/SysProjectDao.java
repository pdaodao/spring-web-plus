package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysProject;
import com.github.pdaodao.springwebplus.mapper.SysProjectMapper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = "ZtProject")
public class SysProjectDao extends BaseDao<SysProjectMapper, SysProject> {
    @Cacheable
    public SysProject byId(final String id){
        Preconditions.checkNotNull(id, "id不能为空");
        return getById(id);
    }

    @Cacheable
    public SysProject byName(final String name){
        Preconditions.checkNotBlank(name, "编码不能为空");
        return getOne(QueryBuilder.lambda(SysProject.class).eq(SysProject::getName, name).build());
    }

    public SysProject byTitle(final String title){
        Preconditions.checkNotBlank(title, "名称不能为空");
        return getOne(QueryBuilder.lambda(SysProject.class).eq(SysProject::getTitle, title).build());
    }

    @Override
    protected void saveCheck(SysProject entity, boolean isInsert) {
        final SysProject old = byName(entity.getName());
        if (old != null) {
            Preconditions.checkArgument(ObjectUtil.equals(entity.getId(), old.getId()), "编码已存在");
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean save(SysProject entity) {
        return super.save(entity);
    }
}
