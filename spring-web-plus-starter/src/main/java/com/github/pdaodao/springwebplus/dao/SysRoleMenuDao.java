package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysRoleMenu;
import com.github.pdaodao.springwebplus.mapper.SysRoleMenuMapper;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SysRoleMenuDao extends BaseDao<SysRoleMenuMapper, SysRoleMenu> {

    /**
     * 删除菜单时要删除分配的菜单项
     *
     * @param ids
     * @return
     */
    public boolean deleteByMenuId(final Collection<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return true;
        }
        return remove(QueryBuilder.lambda(SysRoleMenu.class)
                .in(SysRoleMenu::getMenuId, ids).build());
    }

    public boolean saveRoleMenus(final String roleId, final List<String> menuIds){
        if(CollUtil.isEmpty(menuIds)){
            return false;
        }
        remove(QueryBuilder.lambda(SysRoleMenu.class).eq(SysRoleMenu::getRoleId, roleId).build());
        final List<SysRoleMenu> list = new ArrayList<>();
        for(final String m: menuIds){
            final SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(m);
            list.add(rm);
        }
        return saveBatch(list);
    }

    public List<String> roleMenuIds(final String roleId){
        final List<SysRoleMenu> list = list(QueryBuilder.lambda(SysRoleMenu.class)
                .eq(SysRoleMenu::getRoleId, roleId).build());
        return list.stream().map(t -> t.getMenuId()).collect(Collectors.toList());
    }
}