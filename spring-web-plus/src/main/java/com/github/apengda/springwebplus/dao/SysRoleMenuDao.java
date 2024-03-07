package com.github.apengda.springwebplus.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.github.apengda.springwebplus.entity.SysRoleMenu;
import com.github.apengda.springwebplus.mapper.SysRoleMenuMapper;
import com.github.apengda.springwebplus.starter.dao.BaseDao;
import com.github.apengda.springwebplus.starter.query.QueryBuilder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class SysRoleMenuDao extends BaseDao<SysRoleMenuMapper, SysRoleMenu> {

    /**
     * 删除菜单时要删除分配的菜单项
     * @param ids
     * @return
     */
    public boolean deleteByMenuId(final Collection<String> ids){
        if(CollectionUtil.isEmpty(ids)){
            return true;
        }
        return remove(QueryBuilder.lambda(SysRoleMenu.class)
                .in(SysRoleMenu::getMenuId, ids).build());
    }
}