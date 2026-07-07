package com.github.pdaodao.springwebplus.service;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.pdaodao.springwebplus.base.auth.SysLogAop;
import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.LoginUserInfo;
import com.github.pdaodao.springwebplus.entity.SysRole;
import com.github.pdaodao.springwebplus.entity.SysUser;
import com.github.pdaodao.springwebplus.pojo.UserPassword;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;

@Service
@AllArgsConstructor
public class SysUserLoginService implements LoginService {
    private final SysUserService sysUserService;
    private final Cache<String, Integer> loginErrorMap = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .build();

    @Override
    public CurrentUserInfo login(final LoginUserInfo loginInfo) {
        Preconditions.checkNotBlank(loginInfo.getUsername(), "用户名为空.");
        Preconditions.checkNotBlank(loginInfo.getPassword(), "密码为空.");
        Integer errorSize = loginErrorMap.getIfPresent(loginInfo.getUsername());
        Preconditions.assertTrue(errorSize != null && errorSize >= 5, "错误登录次数达到5次锁定30分钟");
        final SysUser sysUser = sysUserService.infoWithRole(null, loginInfo.getUsername());
        Preconditions.checkNotNull(sysUser, "用户不存在.");
        // 校验密码
        final String encryptPassword = PasswordUtil.encrypt(loginInfo.getPassword(), sysUser.getSalt());
        final boolean success = StrUtil.equals(encryptPassword, sysUser.getPassword());
        if(success){
            loginErrorMap.invalidate(loginInfo.getUsername());
        }else{
            if(errorSize == null){
                errorSize = 0;
            }
            errorSize += 1;
            loginErrorMap.put(loginInfo.getUsername(), errorSize);
        }
        Preconditions.checkArgument(success, "账号密码错误");
        final CurrentUserInfo result = new CurrentUserInfo();
        result.setId(sysUser.getId());
        result.setUsername(sysUser.getUsername());
        result.setUserNickname(sysUser.getNickname());
        result.setAvatar(sysUser.getAvatar());
        sysUserService.updateLoginTime(sysUser.getId(), SysLogAop.getLog().getIp());
        return result;
    }

    @Override
    public Boolean updatePassword(UserPassword userPassword) {
        final SysUser sysUser = sysUserService.getById(userPassword.getUserId());
        Preconditions.checkNotNull(sysUser, "用户不存在.");
        Preconditions.checkArgument(StrUtil.length(userPassword.getPassword()) >=8, "密码长度至少为8位");
        Integer errorSize = loginErrorMap.getIfPresent(sysUser.getUsername());
        Preconditions.assertTrue(errorSize != null && errorSize >= 5, "密码错误次数达到5次锁定30分钟");
        final String encryptOldPassword = PasswordUtil.encrypt(userPassword.getOldPassword(), sysUser.getSalt());
        final String encryptPassword = PasswordUtil.encrypt(userPassword.getPassword(), sysUser.getSalt());
        Preconditions.assertTrue(StrUtil.equals(encryptOldPassword, encryptPassword), "与原密码相同");
        final boolean oldSuccess = StrUtil.equals(encryptOldPassword, sysUser.getPassword());
        if(!oldSuccess){
            if(errorSize == null){
                errorSize = 0;
            }
            errorSize += 1;
            loginErrorMap.put(sysUser.getUsername(), errorSize);
            Preconditions.assertTrue(true, "旧密码错误");
        }
        return sysUserService.updatePassword(sysUser.getId(), encryptPassword);
    }

    @Override
    public List<SysRole> userRoles(String userId) {
        return sysUserService.userRoles(userId);
    }

    public static void main(String[] args) {
        final String p = PasswordUtil.encrypt("park123456", "123");
        System.out.println(p);
    }

    @Override
    public void logout(CurrentUserInfo userInfo) {

    }
}
