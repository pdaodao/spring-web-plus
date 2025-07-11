package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysMenu;
import com.github.pdaodao.springwebplus.mapper.SysMenuMapper;
import com.github.pdaodao.springwebplus.query.SysMenuQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@CacheConfig(cacheNames = "SysMenu")
public class SysMenuDao extends BaseDao<SysMenuMapper, SysMenu> {
    @Autowired
    private SysRoleMenuDao roleMenuDao;

    @Cacheable
    public List<SysMenu> allList() {
        return list();
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean save(SysMenu entity) {
        return super.save(entity);
    }

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteById(final String id) {
        final SysMenu sysMenu = getById(id);
        if (sysMenu == null) {
            return true;
        }
        // 查询到子项
        final Set<String> subs = subDeepIds(id);
        subs.add(id);
        roleMenuDao.deleteByMenuId(subs);
        return remove(QueryBuilder.lambda(SysMenu.class)
                .in(SysMenu::getId, subs).build());
    }

    /**
     * 所有的子项列表
     *
     * @param id
     * @return
     */
    private Set<String> subDeepIds(final String id) {
        final Set<String> ids = getIdByPids(ListUtil.list(false, id));
        int size = ids.size();
        for (int i = 0; i < 5; i++) {
            ids.addAll(getIdByPids(ids));
            if (size >= ids.size()) {
                break;
            }
            size = ids.size();
        }
        return ids;
    }

    private Set<String> getIdByPids(final Collection<String> pids) {
        final List<SysMenu> list = byPids(pids);
        return list.stream().map(t -> t.getId()).collect(Collectors.toSet());
    }


    private List<SysMenu> byPids(final Collection<String> pids) {
        if (CollUtil.isEmpty(pids)) {
            return ListUtil.empty();
        }
        return list(QueryBuilder.lambda(SysMenu.class)
                .in(SysMenu::getPid, pids).build());
    }
}
