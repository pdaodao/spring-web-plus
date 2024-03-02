package com.github.apengda.springwebplus.controller;

import cn.hutool.extra.servlet.ServletUtil;
import com.github.apengda.springwebplus.dao.SysMenuDao;
import com.github.apengda.springwebplus.entity.SysMenu;
import com.github.apengda.springwebplus.service.LoginService;
import com.github.apengda.springwebplus.starter.auth.IgnoreLogin;
import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.apengda.springwebplus.starter.pojo.LoginInfo;
import com.github.apengda.springwebplus.starter.service.TokenStore;
import com.github.apengda.springwebplus.starter.util.Preconditions;
import com.github.apengda.springwebplus.starter.util.RequestUtil;
import com.github.apengda.springwebplus.starter.util.TokenUtil;
import com.github.apengda.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "用户登陆")
@RequestMapping(Constant.ApiPrefix + "/login")
@AllArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final TokenStore tokenStore;
    private final SysMenuDao menuDao;

    @PostMapping
    @Operation(summary = "登录")
    @IgnoreLogin
    public CurrentUserInfo login(@Valid @RequestBody LoginInfo loginInfo, HttpServletResponse response) {
        final CurrentUserInfo userInfo = loginService.login(loginInfo);
        final String token = tokenStore.buildToken(userInfo);
        tokenStore.storeToken(token, userInfo);
        final Cookie cookie = new Cookie(TokenUtil.TokenName, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        ServletUtil.addCookie(response, cookie);
        return userInfo;
    }

    @GetMapping("profile")
    @Operation(summary = "当前用户信息")
    public CurrentUserInfo profile() {
        final CurrentUserInfo currentUserInfo = RequestUtil.getCurrentUser();
        Preconditions.checkNotNull(currentUserInfo, "请重新登录.");
        // todo1

        return currentUserInfo;
    }

    @GetMapping("menu")
    @Operation(summary = "我的菜单")
    public List<SysMenu> myMenu() {
        final CurrentUserInfo currentUserInfo = RequestUtil.getCurrentUser();

        return menuDao.menuList();
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Boolean logout(HttpServletResponse response) {
        final String token = TokenUtil.getToken();
        final CurrentUserInfo userInfo = tokenStore.byToken(token);
        loginService.logout(userInfo);
        tokenStore.removeToken(token);
        ServletUtil.addCookie(response, TokenUtil.TokenName, "", 0);
        return true;
    }
}
