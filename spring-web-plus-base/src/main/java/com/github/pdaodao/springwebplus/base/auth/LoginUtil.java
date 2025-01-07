package com.github.pdaodao.springwebplus.base.auth;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.config.SysConfigProperties;
import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.RestCode;
import com.github.pdaodao.springwebplus.base.pojo.RestException;
import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;
import com.github.pdaodao.springwebplus.base.service.TokenStore;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.units.qual.C;

public class LoginUtil {
    /**
     * 校验是否登陆, 未登陆抛出异常
     */
    public static void checkLogin() {
        final String token = getTokenValue();
        if(StrUtil.isBlank(token)){
            throw new RestException(RestCode.NO_USER_INFO, "未能读取到有效 token,请登陆");
        }
        final TokenInfo tokenInfo = tokenStore().byToken(token);
        if(tokenInfo == null){
            throw new RestException(RestCode.NO_USER_INFO, "请重新登陆");
        }
    }

    public static String genTokenValue(final Long userId){
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 判断当前会话是否已经登录
     * @return 已登录返回 true，未登录返回 false
     */
    public static boolean isLogin() {
        final TokenInfo tokenInfo = tokenInfo();
        return tokenInfo != null;
    }

    public static TokenInfo tokenInfo(){
        final String token = getTokenValue();
        if(StrUtil.isBlank(token)){
            return null;
        }
        final TokenInfo info = tokenStore().byToken(token);
        return info;
    }

    public static CurrentUserInfo userInfo(){
        final TokenInfo info = tokenInfo();
        if(info == null){
            return null;
        }
        final CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        currentUserInfo.setId(info.getUserId());
        currentUserInfo.setUsername(info.getUsername());

        final UserAuthService userAuthService = userAuthService();
        if(userAuthService != null){
            final CurrentUserInfo u = userAuthService.userInfo(info.getUserId());
            if(u != null){
                currentUserInfo.setNickname(u.getNickname());
            }
        }
        return currentUserInfo;
    }


    public static UserAuthService userAuthService(){
        return SpringUtil.getBean(UserAuthService.class);
    }


    public static TokenStore tokenStore(){
        return SpringUtil.getBean(TokenStore.class);
    }

    /**
     * 获取当前请求的 token 值
     * @return
     */
    public static String getTokenValue() {
        final SysConfigProperties sysConfig = sysConfig();
        final HttpServletRequest req = RequestUtil.getRequest();
        String token = req.getParameter(sysConfig.getAuthTokenName());
        if(StrUtil.isBlank(token)){
            token = RequestUtil.getFromHead(sysConfig.getAuthTokenName());
        }
        if(StrUtil.isBlank(token)){
            token = RequestUtil.getFromCookie(sysConfig.getAuthTokenName());
        }
        return token;
    }

    public static SysConfigProperties sysConfig(){
        return SpringUtil.getBean(SysConfigProperties.class);
    }
}
