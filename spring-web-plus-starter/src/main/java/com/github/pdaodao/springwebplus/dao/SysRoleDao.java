package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysRole;
import com.github.pdaodao.springwebplus.mapper.SysRoleMapper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@CacheConfig(cacheNames = "SysRole")
public class SysRoleDao extends BaseDao<SysRoleMapper, SysRole> {
    @Autowired
    private SysRoleMenuDao roleMenuDao;

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveWithMenu(final SysRole role){
        super.save(role);
        roleMenuDao.saveRoleMenus(role.getId(), role.getMenuIds());
        return true;
    }

    @Cacheable
    public SysRole info(final String id){
        final SysRole role = getById(id);
        role.setMenuIds(roleMenuDao.roleMenuIds(id));
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    protected void saveCheck(SysRole entity, boolean isInsert) {
        final SysRole old = getOne(QueryBuilder.lambda(SysRole.class)
                .eq(SysRole::getTitle, entity.getTitle())
                .eq(SysRole::getTeamId, entity.getTeamId()).build());
        if (old != null) {
            Preconditions.checkArgument(ObjectUtil.equals(entity.getId(), old.getId()), "名称已存在");
        }
    }

    @CacheEvict(allEntries = true)
    public void clearCache(){
    }
}
