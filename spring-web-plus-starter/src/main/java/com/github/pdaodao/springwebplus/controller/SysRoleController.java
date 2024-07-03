package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.dao.SysRoleDao;
import com.github.pdaodao.springwebplus.entity.SysRole;
import com.github.pdaodao.springwebplus.entity.SysRoleMenu;
import com.github.pdaodao.springwebplus.query.SysRoleQuery;
import com.github.pdaodao.springwebplus.starter.auth.Permission;
import com.github.pdaodao.springwebplus.starter.pojo.PageR;
import com.github.pdaodao.springwebplus.starter.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.starter.util.PageHelper;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "系统角色")
@RequestMapping(Constant.ApiPrefix + "/role")
@AllArgsConstructor
public class SysRoleController {
    private final SysRoleDao roleDao;

    @PostMapping("/add")
    @Operation(summary = "添加系统角色")
    @Permission("sys:role:add")
    public Boolean addSysRole(@Valid @RequestBody SysRole role) {
        return roleDao.save(role);
    }

    @PostMapping("/update")
    @Operation(summary = "修改系统角色")
    @Permission("sys:role:update")
    public Boolean updateSysRole(@Valid @RequestBody SysRole role) {
        return roleDao.save(role);
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除系统角色")
    @Permission("sys:role:delete")
    public Boolean deleteSysRole(@PathVariable String id) {
        return roleDao.removeById(id);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "系统角色详情")
    @Permission("sys:role:info")
    public SysRole getSysRole(@PathVariable String id) {
        return roleDao.getById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "系统角色分页列表")
    @Permission("sys:role:page")
    public PageR<SysRole> getSysRolePage(SysRoleQuery query,
                                         final PageRequestParam pageRequestParam) {
        try (final PageHelper pageHelper = PageHelper.startPage(pageRequestParam)) {
            List<SysRole> list = roleDao.list();
            return pageHelper.toPageResult(list);
        }
    }

    @GetMapping("/list")
    @Operation(summary = "系统所有角色列表")
    @Permission("sys:role:page")
    public List<SysRole> getSysRoleAllList() {
        return roleDao.list();
    }


    @PostMapping("/set/menu")
    @Operation(summary = "设置角色权限")
    @Permission("sys:role:set-role-menus")
    public Boolean setRoleMenus(@Valid @RequestBody SysRoleMenu roleMenu) {
//        boolean flag = sysRoleService.setRoleMenus(roleMenusDto);
//        return ApiResult.success(flag);
        return true;
    }


    @GetMapping("/{roleId}/menu-ids")
    @Operation(summary = "获取角色权限ID集合")
    public List<String> getMenuIdsByRoleId(@PathVariable Long roleId) {
//        List<Long> list = sysMenuService.getMenuIdsByRoleId(roleId);
//        return ApiResult.success(list);
        return new ArrayList<>();
    }


}
