package com.github.pdaodao.springwebplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.LoginUserInfo;
import com.github.pdaodao.springwebplus.entity.SysUser;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SysUserLoginService implements LoginService {
    private final SysUserService sysUserService;

    @Override
    public CurrentUserInfo login(final LoginUserInfo loginInfo) {
        Preconditions.checkNotBlank(loginInfo.getUsername(), "用户名为空.");
        Preconditions.checkNotBlank(loginInfo.getPassword(), "密码为空.");
        final SysUser sysUser = sysUserService.infoWithRole(null, loginInfo.getUsername());
        Preconditions.checkNotNull(sysUser, "用户不存在.");
        // 校验密码
        final String encryptPassword = PasswordUtil.encrypt(loginInfo.getPassword(), sysUser.getSalt());
        Preconditions.checkArgument(StrUtil.equals(encryptPassword, sysUser.getPassword()), "账号密码错误");

        final CurrentUserInfo result = new CurrentUserInfo();
        result.setId(sysUser.getId());
        result.setUsername(sysUser.getUsername());
        result.setNickname(sysUser.getNickname());
        if (CollUtil.isNotEmpty(sysUser.getRoleList())) {
            result.setRoles(sysUser.getRoleList().stream().map(t -> t.getIdCode()).collect(Collectors.toSet()));
        }
        return result;
    }

    @Override
    public void logout(CurrentUserInfo userInfo) {

    }
}
