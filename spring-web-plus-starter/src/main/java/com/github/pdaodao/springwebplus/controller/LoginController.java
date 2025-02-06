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
import com.github.pdaodao.springwebplus.service.LoginService;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Tag(name = "用户登录")
@RequestMapping(Constant.ApiPrefix + "/login")
@AllArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final SysMenuDao menuDao;

    @PostMapping
    @IgnoreLogin
    @Operation(summary = "登录")
    public CurrentUserInfo login(@Valid @RequestBody LoginUserInfo loginInfo, HttpServletResponse response) throws Exception{
        final CurrentUserInfo userInfo = loginService.login(loginInfo);
        final TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId(userInfo.getId());
        tokenInfo.setUsername(userInfo.getUsername());
        tokenInfo.setDevice("PC");
        LoginUtil.login(tokenInfo);
        userInfo.setToken(tokenInfo.getToken());
        return userInfo;
    }

    @GetMapping("profile")
    @Operation(summary = "当前用户信息")
    public RichUserInfo profile() {
        final CurrentUserInfo currentUserInfo = RequestUtil.getCurrentUser();
        Preconditions.checkNotNull(currentUserInfo, "请重新登录.");

        final RichUserInfo rr = new RichUserInfo();
        BeanUtils.copyProperties(currentUserInfo, rr);
        // todo1
        return rr;
    }

    @GetMapping("menu")
    @Operation(summary = "我的菜单")
    public List<SysMenu> myMenu() {
        final CurrentUserInfo currentUserInfo = RequestUtil.getCurrentUser();
        final List<SysMenu> ms = menuDao.menuList();
        return IdUtil.toTree(ms, SysMenu::getId, SysMenu::getPid);
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
}
