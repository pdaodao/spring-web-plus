package com.github.apengda.springwebplus.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.apengda.springwebplus.entity.SysMenu;
import com.github.apengda.springwebplus.mapper.SysMenuMapper;
import com.github.apengda.springwebplus.starter.dao.BaseDao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SysMenuDao extends BaseDao<SysMenuMapper, SysMenu> {

    public List<SysMenu> list(final List<Integer> types) {
        return list(Wrappers.lambdaUpdate(SysMenu.class)
                .in(CollUtil.isNotEmpty(types), SysMenu::getType, types)
                .orderByAsc(SysMenu::getSeq));
    }

    public List<SysMenu> menuList() {
        return list(ListUtil.of(1, 2));
    }
}
