package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.entity.SysUser;
import com.github.pdaodao.springwebplus.query.SysUserQuery;
import com.github.pdaodao.springwebplus.service.SysUserService;
import com.github.pdaodao.springwebplus.base.auth.Permission;
import com.github.pdaodao.springwebplus.base.pojo.PageResult;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "系统用户")
@RequestMapping(Constant.ApiPrefix + "/user")
@AllArgsConstructor
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/add")
    @Operation(summary = "添加用户")
    @Permission("sys:user:add")
    public Boolean addSysUser(@Valid @RequestBody SysUser sysUser) {
        return sysUserService.saveUser(sysUser);
    }

    @PostMapping("/update")
    @Operation(summary = "修改系统用户")
    @Permission("sys:user:update")
    public Boolean updateSysUser(@Valid @RequestBody SysUser user) {
        return sysUserService.saveUser(user);
    }


    @PostMapping("/delete/{id}")
    @Operation(summary = "删除系统用户")
    @Permission("sys:user:delete")
    public Boolean deleteSysUser(@PathVariable String id) {
        return sysUserService.deleteById(id);
    }

    @Operation(summary = "系统用户详情")
    @GetMapping("/info/{id}")
    @Permission("sys:user:info")
    public SysUser getSysUser(@PathVariable String id) {
        return sysUserService.infoWithRole(id, null);
    }

    @Operation(summary = "系统用户分页列表")
    @GetMapping("/page")
    @Permission("sys:user:page")
    public PageResult<SysUser> listPage(SysUserQuery userQuery, PageRequestParam pageRequestParam) {
        try (final PageHelper pageHelper = PageHelper.startPage(pageRequestParam)) {
            final List<SysUser> users = sysUserService.list(userQuery);
            return pageHelper.toPageResult(users);
        }
    }

    @Operation(summary = "重置用户密码")
    @PostMapping("/reset-password")
    @Permission("sys:user:reset-password")
    public Boolean resetSysUserPassword(@Valid @RequestBody SysUser sysUser) {
        return sysUserService.updatePassword(sysUser);
    }

    @PostMapping("/update-profile")
    @Operation(summary = "修改个人信息")
    public Boolean updateProfile(@Valid @RequestBody SysUser sysUser) {
        boolean flag = sysUserService.saveUser(sysUser);
        return flag;
    }

    @Operation(summary = "修改用户密码")
    @PostMapping("/updatePassword")
    public Boolean updatePassword(@Valid @RequestBody SysUser sysUser) {
        boolean flag = sysUserService.updatePassword(sysUser);
        return flag;
    }

//    @PostMapping("/importExcel")
//    @Operation(summary = "导入Excel用户数据")
//    public Boolean importExcel(MultipartFile multipartFile) throws Exception {
//        boolean flag = sysUserService.importExcel(multipartFile);
//        return flag;
//    }

}
