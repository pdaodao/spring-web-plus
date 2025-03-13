package com.github.pdaodao.springwebplus.base.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.github.pdaodao.springwebplus.base.config.SysConfigProperties;
import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.RestCode;
import com.github.pdaodao.springwebplus.base.pojo.RestException;
import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;
import com.github.pdaodao.springwebplus.base.service.TokenStore;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public class LoginUtil {
    private static final ThreadLocal<TokenInfo> tokenHolder = new ThreadLocal<>();

    public static void clearHolder(){
        tokenHolder.remove();;
    }

    /**
     * 判断当前会话是否已经登录
     * @return 已登录返回 true，未登录返回 false
     */
    public static boolean isLogin() throws Exception{
        final TokenInfo tokenInfo = tokenInfo();
        if(tokenInfo == null){
            return false;
        }
        if(isTokenExpired(tokenInfo)){
            return false;
        }
        return true;
    }

    /**
     * 校验是否登陆, 未登陆抛出异常
     */
    public static void checkLogin() throws Exception{
        final TokenInfo tokenInfo = tokenInfo();
        if(tokenInfo == null){
            throw new RestException(RestCode.NO_USER_INFO, "请重新登陆");
        }
        if(isTokenExpired(tokenInfo)){
            throw new RestException(RestCode.NO_USER_INFO, "登陆已过期请重新登陆");
        }
    }

    public static void login(final TokenInfo tokenInfo) throws Exception{
        Preconditions.checkNotNull(tokenInfo, "tokenInfo is null.");
        Preconditions.checkNotNull(tokenInfo.getUserId(), "token用户信息为空.");
        if(StrUtil.isBlank(tokenInfo.getToken())){
            final String token = tokenInfo.getUserId()+"-"+IdUtil.fastSimpleUUID();
            tokenInfo.setToken(token);
        }
        tokenInfo.setTokenTimeout(sysConfig().authExpireSeconds());
        tokenInfo.setTokenActiveTimeout(sysConfig().authActiveSeconds());
        final TokenStore tokenStore = tokenStore();
        if(tokenInfo.getToken().contains("-")){
            final String prefix = tokenInfo.getToken().substring(0, tokenInfo.getToken().indexOf("-"));
            final List<TokenInfo> oldList = tokenStore.byPrefix(prefix);
            if(CollUtil.isNotEmpty(oldList)){
                for(final TokenInfo old: oldList){
                    if(isTokenExpired(old)){
                        tokenStore.removeToken(old.getToken());
                    }
                }
            }
        }
        tokenStore().storeToken(tokenInfo.getToken(), tokenInfo);
        final Cookie cookie = new Cookie(sysConfig().getAuthTokenName(), tokenInfo.token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        JakartaServletUtil.addCookie(RequestUtil.getResponse(), cookie);
    }

    public static TokenInfo logout() throws Exception{
        final TokenInfo tokenInfo = tokenInfo();
        if(tokenInfo != null){
            tokenStore().removeToken(tokenInfo.getToken());
            JakartaServletUtil.addCookie(RequestUtil.getResponse(), sysConfig().getAuthTokenName(), "", 0);
        }
        return tokenInfo;
    }

    public static TokenInfo tokenInfo() throws Exception{
        TokenInfo info = tokenHolder.get();
        if(info != null){
            return info;
        }
        final String token = getTokenValue();
        if(StrUtil.isBlank(token)){
            return null;
        }
        info = tokenStore().byToken(token);
        tokenHolder.set(info);
        return info;
    }

    public static TokenInfo tokenInfo(final String token) throws Exception{
        if(StrUtil.isBlank(token)){
            return null;
        }
        if(StrUtil.isBlank(token)){
            return null;
        }
        final TokenInfo info = tokenStore().byToken(token);
        tokenHolder.set(info);
        return info;
    }

    public static CurrentUserInfo userInfo() throws Exception{
        final TokenInfo info = tokenInfo();
        return userInfo(info);
    }

    public static CurrentUserInfo userInfo( final TokenInfo info) throws Exception{
        if(info == null){
            return null;
        }
        final CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        currentUserInfo.setToken(info.getToken());
        currentUserInfo.setId(info.getUserId());
        currentUserInfo.setUsername(info.getUsername());
        final UserAuthService userAuthService = userAuthService();
        if(userAuthService != null){
            final CurrentUserInfo u = userAuthService.userInfo(info.getUserId());
            if(u != null){
                currentUserInfo.setName(u.getName());
            }
        }
        currentUserInfo.setTeamId(getTeam());
        return currentUserInfo;
    }


    public static UserAuthService userAuthService(){
        try{
            return SpringUtil.getBean(UserAuthService.class);
        }catch (Exception e){
            return null;
        }
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

    /**
     * 获取团队id
     * @return
     */
    public static String getTeam(){
        final SysConfigProperties sysConfig = sysConfig();
        String team = RequestUtil.getFromHead(sysConfig.getAuthTeam());
        if(StrUtil.isBlank(team)){
            team = RequestUtil.getFromCookie(sysConfig.getAuthTeam());
        }
        return team;
    }

    public static SysConfigProperties sysConfig(){
        return SpringUtil.getBean(SysConfigProperties.class);
    }

    /**
     * token是否过期
     * @param tokenInfo
     * @return
     */
    public static boolean isTokenExpired(final TokenInfo tokenInfo){
        Preconditions.checkNotNull(tokenInfo, "token信息为空.");
        if(tokenInfo.getLoginTime() == null){
            return true;
        }
        final Long now = System.currentTimeMillis();
        final SysConfigProperties sysConfig = sysConfig();
        if(sysConfig.getAuthExpire() > 3){
            if(DateTimeUtil.addMinutes(tokenInfo.getLoginTime(), sysConfig.getAuthExpire()).getTime() < now){
                return true;
            }
        }
        if(tokenInfo.getLastAccessTime() != null && sysConfig.getAuthActive() > 3){
            if(DateTimeUtil.addMinutes(tokenInfo.getLastAccessTime(), sysConfig.getAuthActive()).getTime() < now){
                return true;
            }
        }
        return false;
    }
}
