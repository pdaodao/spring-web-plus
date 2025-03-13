package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.base.auth.Permission;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.dao.SysMenuDao;
import com.github.pdaodao.springwebplus.entity.SysMenu;
import com.github.pdaodao.springwebplus.query.SysMenuQuery;
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

    @PostMapping("/add")
    @Operation(summary = "添加系统菜单")
    @Permission("sys:menu:add")
    public Boolean addSysMenu(@Valid @RequestBody SysMenu menu) {
        return sysMenuDao.save(menu);

    }

    @PostMapping("/update")
    @Operation(summary = "修改系统菜单")
    @Permission("sys:menu:update")
    public Boolean updateSysMenu(@Valid @RequestBody SysMenu menu) {
        return sysMenuDao.save(menu);
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除系统菜单")
    @Permission("sys:menu:delete")
    public Boolean deleteSysMenu(@PathVariable(name = "id") String id) {
        return sysMenuDao.deleteById(id);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "系统菜单详情")
    @Permission("sys:menu:info")
    public SysMenu getSysMenu(@PathVariable(name = "id") Long id) {
        return sysMenuDao.getById(id);
    }

    @GetMapping("/tree")
    @Operation(summary = "树形列表")
    @Permission("sys:menu:all-tree-list")
    public List<SysMenu> getAllSysMenuTreeList(final SysMenuQuery query) {
        final List<SysMenu> list = sysMenuDao.allList(query);
        return IdUtil.toTree(list, SysMenu::getId, SysMenu::getPid);
    }

    @GetMapping("/tree/enabled")
    @Operation(summary = "启用的菜单树")
    @Permission("sys:menu:tree-list")
    public List<SysMenu> getSysMenuTreeList() {
        final SysMenuQuery query = new SysMenuQuery();
        query.setEnabled(true);
        return getAllSysMenuTreeList(query);
    }
}
