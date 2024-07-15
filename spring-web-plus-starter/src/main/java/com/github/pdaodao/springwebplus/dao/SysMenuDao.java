package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pdaodao.springwebplus.entity.SysMenu;
import com.github.pdaodao.springwebplus.mapper.SysMenuMapper;
import com.github.pdaodao.springwebplus.query.SysMenuQuery;
import com.github.pdaodao.springwebplus.starter.dao.BaseDao;
import com.github.pdaodao.springwebplus.starter.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SysMenuDao extends BaseDao<SysMenuMapper, SysMenu> {
    @Autowired
    private SysRoleMenuDao roleMenuDao;

    public List<SysMenu> list(final List<Integer> types) {
        return list(Wrappers.lambdaUpdate(SysMenu.class)
                .in(CollUtil.isNotEmpty(types), SysMenu::getType, types)
                .orderByAsc(SysMenu::getSeq));
    }

    public List<SysMenu> menuList() {
        return list(ListUtil.of(1, 2));
    }

    public List<SysMenu> allList(final SysMenuQuery query) {
        return list(QueryBuilder.lambda(SysMenu.class)
                .like(query.getKeyword(), SysMenu::getName, SysMenu::getComponentPath, SysMenu::getRouteUrl)
                .eq(SysMenu::getIsShow, query.getIsShow())
                .eq(SysMenu::getEnabled, query.getEnabled())
                .build().orderByAsc(SysMenu::getSeq));
    }

    /**
     * 用户菜单
     *
     * @param userId
     * @return
     */
    public List<SysMenu> userMenu(final String userId) {
        return baseMapper.userMenu(userId);
    }

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
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
