package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.base.auth.Permission;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.dao.SysMenuDao;
import com.github.pdaodao.springwebplus.dao.SysRoleDao;
import com.github.pdaodao.springwebplus.entity.SysMenu;
import com.github.pdaodao.springwebplus.entity.SysRole;
import com.github.pdaodao.springwebplus.query.SysMenuQuery;
import com.github.pdaodao.springwebplus.query.SysRoleQuery;
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
@Tag(name = "系统角色")
@RequestMapping(Constant.ApiPrefix + "/role")
@AllArgsConstructor
public class SysRoleController {
    private final SysRoleDao roleDao;
    private final SysMenuDao menuDao;

    @GetMapping("/page")
    @Operation(summary = "系统角色分页列表")
    @Permission("sys:role:list")
    public List<SysRole> getSysRolePage(SysRoleQuery query,
                                        final PageRequestParam pageRequestParam) {
        PageHelper.startPage(pageRequestParam);
        final List<SysRole> list = roleDao.list(QueryBuilder.lambda(SysRole.class)
                .eq(SysRole::getTeamId, RequestUtil.getTeamOrDefault())
                .eq(SysRole::getIsSystem, query.getIsSystem())
                .like(pageRequestParam.getQ(), SysRole::getTitle, SysRole::getName)
                .build());
        return list;
    }

    @GetMapping("/list")
    @Operation(summary = "系统所有角色列表")
    @Permission("sys:role:list")
    public List<SysRole> getSysRoleAllList() {
        return roleDao.list();
    }

    @PostMapping("/save")
    @Operation(summary = "保存系统角色")
    @Permission("sys:role:save")
    public Boolean addSysRole(@Valid @RequestBody SysRole role) {
        return roleDao.saveWithMenu(role);
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除系统角色")
    @Permission("sys:role:delete")
    public Boolean deleteSysRole(@PathVariable(name = "id") String id) {
        return roleDao.removeById(id);
    }

    @GetMapping("/info")
    @Operation(summary = "系统角色详情")
    @Permission("sys:role:info")
    public SysRole getSysRole(final String id) {
        final SysRole role = roleDao.info(id);
        if(role != null){
            final SysMenuQuery query = new SysMenuQuery();
            final List<SysMenu> list = BeanUtils.copyToList(menuDao.allList(), SysMenu.class);
            final List<SysMenu> tree = IdUtil.toTree(list, SysMenu::getId, SysMenu::getPid);
            role.setMenus(tree);
        }
        return role;
    }
}
