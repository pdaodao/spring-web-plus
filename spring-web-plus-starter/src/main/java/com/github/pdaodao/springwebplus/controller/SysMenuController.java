package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.base.auth.Permission;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.dao.SysMenuDao;
import com.github.pdaodao.springwebplus.dao.SysRoleDao;
import com.github.pdaodao.springwebplus.entity.SysMenu;
import com.github.pdaodao.springwebplus.query.SysMenuQuery;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "系统菜单")
@RequestMapping(Constant.ApiPrefix + "/menu")
@AllArgsConstructor
public class SysMenuController {
    private final SysMenuDao sysMenuDao;
    private final SysRoleDao roleDao;

    @PostMapping("/save")
    @Operation(summary = "保存系统菜单")
    @Permission("sys:menu:save")
    public SysMenu addSysMenu(@Valid @RequestBody SysMenu menu) {
        sysMenuDao.save(menu);
        roleDao.clearCache();
        return menu;
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除系统菜单")
    @Permission("sys:menu:save")
    public Boolean deleteSysMenu(@PathVariable(name = "id") String id) {
        sysMenuDao.deleteById(id);
        roleDao.clearCache();
        return true;
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "系统菜单详情")
    @Permission("sys:menu:info")
    public SysMenu getSysMenu(@PathVariable(name = "id") String id) {
        return sysMenuDao.getById(id);
    }

    @GetMapping("/tree")
    @Operation(summary = "树形列表")
    @Permission("sys:menu:list")
    public List<SysMenu> menuTree() {
        final List<SysMenu> list = BeanUtils.copyToList(sysMenuDao.allList(), SysMenu.class);
        return IdUtil.toTree(list, SysMenu::getId, SysMenu::getPid);
    }

//    @GetMapping("/tree/enabled")
//    @Operation(summary = "启用的菜单树")
//    @Permission("sys:menu:tree-list")
//    public List<SysMenu> getSysMenuTreeList() {
//        final SysMenuQuery query = new SysMenuQuery();
//        query.setEnabled(true);
//        return getAllSysMenuTreeList(query);
//    }
}
