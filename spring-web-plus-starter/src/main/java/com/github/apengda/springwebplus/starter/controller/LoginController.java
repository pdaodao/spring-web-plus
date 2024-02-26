package com.github.apengda.springwebplus.starter.controller;

import cn.hutool.extra.servlet.ServletUtil;
import com.github.apengda.springwebplus.starter.auth.IgnoreLogin;
import com.github.apengda.springwebplus.starter.pojo.LoginInfo;
import com.github.apengda.springwebplus.starter.service.LoginService;
import com.github.apengda.springwebplus.starter.util.TokenUtil;
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
@Tag(name = "用户信息")
@RequestMapping("/api/v1/login")
@AllArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "登录")
    @IgnoreLogin
    public String login(@Valid @RequestBody LoginInfo loginInfo, HttpServletResponse response) {
        final String token = loginService.login(loginInfo);
        ServletUtil.addCookie(response, TokenUtil.TokenName, token);
        return token;
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Boolean logout(HttpServletResponse response) {
        loginService.logout();
        ServletUtil.addCookie(response, TokenUtil.TokenName, "", 0);
        return true;
    }
}
