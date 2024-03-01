package com.github.apengda.springwebplus.controller;

import cn.hutool.extra.servlet.ServletUtil;
import com.github.apengda.springwebplus.service.LoginService;
import com.github.apengda.springwebplus.starter.auth.IgnoreLogin;
import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.apengda.springwebplus.starter.pojo.LoginInfo;
import com.github.apengda.springwebplus.starter.service.TokenStore;
import com.github.apengda.springwebplus.starter.util.TokenUtil;
import com.github.apengda.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@Tag(name = "用户登陆")
@RequestMapping(Constant.ApiPrefix + "/login")
@AllArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final TokenStore tokenStore;

    @PostMapping
    @Operation(summary = "登录")
    @IgnoreLogin
    public CurrentUserInfo login(@Valid @RequestBody LoginInfo loginInfo, HttpServletResponse response) {
        final CurrentUserInfo userInfo = loginService.login(loginInfo);
        final String token = tokenStore.buildToken(userInfo);
        tokenStore.storeToken(token, userInfo);
        ServletUtil.addCookie(response, TokenUtil.TokenName, token);
        return userInfo;
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
