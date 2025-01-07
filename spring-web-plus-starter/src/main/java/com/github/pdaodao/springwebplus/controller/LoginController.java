//package com.github.pdaodao.springwebplus.controller;
//
//import cn.dev33.satoken.annotation.SaIgnore;
//import cn.dev33.satoken.stp.SaLoginConfig;
//import cn.dev33.satoken.stp.SaLoginModel;
//import cn.dev33.satoken.stp.SaTokenInfo;
//import cn.dev33.satoken.stp.StpUtil;
//import cn.hutool.extra.servlet.JakartaServletUtil;
//import com.github.pdaodao.springwebplus.base.auth.IgnoreLogin;
//import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
//import com.github.pdaodao.springwebplus.base.pojo.LoginUserInfo;
//import com.github.pdaodao.springwebplus.base.service.TokenStore;
//import com.github.pdaodao.springwebplus.base.util.IdUtil;
//import com.github.pdaodao.springwebplus.base.util.RequestUtil;
//import com.github.pdaodao.springwebplus.base.util.TokenUtil;
//import com.github.pdaodao.springwebplus.dao.SysMenuDao;
//import com.github.pdaodao.springwebplus.entity.SysMenu;
//import com.github.pdaodao.springwebplus.service.LoginService;
//import com.github.pdaodao.springwebplus.tool.util.Preconditions;
//import com.github.pdaodao.springwebplus.util.Constant;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@Tag(name = "用户登录")
//@RequestMapping(Constant.ApiPrefix + "/login")
//@AllArgsConstructor
//public class LoginController {
//    private final LoginService loginService;
//    private final TokenStore tokenStore;
//    private final SysMenuDao menuDao;
//
//    @PostMapping
//    @Operation(summary = "登录")
//    @IgnoreLogin
//    @SaIgnore
//    public SaTokenInfo login(@Valid @RequestBody LoginUserInfo loginInfo, HttpServletResponse response) {
//        final CurrentUserInfo userInfo = loginService.login(loginInfo);
//        final String token = tokenStore.buildToken(userInfo);
//        tokenStore.storeToken(token, userInfo);
//        final Cookie cookie = new Cookie(TokenUtil.TokenName, token);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//        JakartaServletUtil.addCookie(response, cookie);
//
//
//        // 记住我--->`SaLoginModel`为登录参数Model，其有诸多参数决定登录时的各种逻辑
//        final SaLoginModel saLoginModel = SaLoginConfig
//                .setDevice("PC")
//                .setIsLastingCookie(true)
//                .setIsWriteHeader(true);
//        // 7 天
//        saLoginModel.setTimeout(60 * 60 * 24 * 7);
//
//        //加入权限和角色
////        List<String> roleList = StpUtil.getRoleList(admin.getId());
////        saLoginModel.setExtra("roles", roleList);
////        List<String> permissionList = StpUtil.getPermissionList(admin.getId());
////        saLoginModel.setExtra("permissions", permissionList);
//        //这里的id是admin的id主键
//        StpUtil.login(userInfo.getId(), saLoginModel);
//        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
//
//        return tokenInfo;
//        // return userInfo;
//    }
//
//    @GetMapping("profile")
//    @Operation(summary = "当前用户信息")
//    public CurrentUserInfo profile() {
//        final CurrentUserInfo currentUserInfo = RequestUtil.getCurrentUser();
//        Preconditions.checkNotNull(currentUserInfo, "请重新登录.");
//        // todo1
//
//        return currentUserInfo;
//    }
//
//    @GetMapping("menu")
//    @Operation(summary = "我的菜单")
//    public List<SysMenu> myMenu() {
//        final CurrentUserInfo currentUserInfo = RequestUtil.getCurrentUser();
//        final List<SysMenu> ms = menuDao.menuList();
//        return IdUtil.toTree(ms, SysMenu::getId, SysMenu::getPid);
//    }
//
//    @PostMapping("/logout")
//    @Operation(summary = "退出登录")
//    public Boolean logout(HttpServletResponse response) {
//        final String token = TokenUtil.getToken();
//        final CurrentUserInfo userInfo = tokenStore.byToken(token);
//        loginService.logout(userInfo);
//        tokenStore.removeToken(token);
//        JakartaServletUtil.addCookie(response, TokenUtil.TokenName, "", 0);
//        return true;
//    }
//}
