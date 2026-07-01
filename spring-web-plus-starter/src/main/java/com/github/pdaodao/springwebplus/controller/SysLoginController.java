package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.base.auth.IgnoreLogin;
import com.github.pdaodao.springwebplus.base.auth.LoginUtil;
import com.github.pdaodao.springwebplus.base.pojo.RichUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.LoginUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.dao.SysMenuDao;
import com.github.pdaodao.springwebplus.entity.SysMenu;
import com.github.pdaodao.springwebplus.entity.SysRole;
import com.github.pdaodao.springwebplus.entity.SysUser;
import com.github.pdaodao.springwebplus.pojo.UserPassword;
import com.github.pdaodao.springwebplus.service.LoginService;
import com.github.pdaodao.springwebplus.service.SysUserService;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.Constant;
import com.github.pdaodao.springwebplus.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@Tag(name = "用户登录")
@RequestMapping(Constant.ApiPrefix + "/login")
@AllArgsConstructor
public class SysLoginController {
    private final LoginService loginService;
    private final SysUserService sysUserService;
    private final SysMenuDao menuDao;

    @PostMapping
    @IgnoreLogin
    @Operation(summary = "登录")
    public CurrentUserInfo login(@Valid @RequestBody LoginUserInfo loginInfo, HttpServletResponse response) throws Exception{
        final CurrentUserInfo userInfo = loginService.login(loginInfo);
        final TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId(userInfo.getId());
        tokenInfo.setUsername(userInfo.getUsername());
        tokenInfo.setUserNickname(userInfo.getUserNickname());
        tokenInfo.setAvatar(userInfo.getAvatar());
        tokenInfo.setDevice("PC");
        LoginUtil.login(tokenInfo);
        userInfo.setToken(tokenInfo.getToken());
        RequestUtil.setCurrentUser(userInfo);
        return userInfo;
    }

    @GetMapping("profile")
    @Operation(summary = "当前用户信息")
    public RichUserInfo profile() {
        final CurrentUserInfo currentUserInfo = RequestUtil.getCurrentUser();
        Preconditions.checkNotNull(currentUserInfo, "请重新登录.");
        final RichUserInfo rr = new RichUserInfo();
        BeanUtils.copyProperties(currentUserInfo, rr);

        final List<SysRole> roles = loginService.userRoles(rr.getId());
        rr.setRoles(BeanUtils.copyToList(roles, SysRole.class, "menuIds", "menus"));
        final List<SysMenu> allMenus = BeanUtils.copyToList(menuDao.allList(), SysMenu.class);
        List<SysMenu> menus = allMenus;
        if(!UserUtil.isRoleInfoRoot(roles)){
            final Set<String> ids = new HashSet<>();
            for(final SysRole r: roles){
                if(r.getMenuIds() != null){
                    ids.addAll(r.getMenuIds());
                }
            }
            final SysMenu root = new SysMenu();
            SysMenu.collectByIds(allMenus, ids, root);
            menus = root.getChildren();
        }
        final List<SysMenu> menuTree = IdUtil.toTree(menus, SysMenu::getId, SysMenu::getPid);;
        rr.setMenus(menuTree);
        return rr;
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Boolean logout(HttpServletResponse response) throws Exception{
        final TokenInfo tokenInfo = LoginUtil.logout();
        if(tokenInfo == null){
            return false;
        }
        loginService.logout(RequestUtil.getCurrentUser());
        return true;
    }

    @Operation(summary = "修改密码")
    @PostMapping("/updatePassword")
    public Boolean updatePassword(@Valid @RequestBody UserPassword userPassword) {
        userPassword.setUserId(RequestUtil.getUserId());
        return loginService.updatePassword(userPassword);
    }
}
