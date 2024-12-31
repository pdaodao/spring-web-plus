package com.github.pdaodao.springwebplus.base.auth;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.pdaodao.springwebplus.base.config.SysConfigProperties;
import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.RestCode;
import com.github.pdaodao.springwebplus.base.pojo.RestException;
import com.github.pdaodao.springwebplus.base.service.TokenStore;
import com.github.pdaodao.springwebplus.base.util.ExceptionUtil;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.base.util.TokenUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@AllArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    private final SysConfigProperties sysConfig;
    private final TokenStore tokenStoreService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final String path = request.getServletPath();
        // 不需要验签
        // 在配置的排除路径中, 带有IgnoreLogin注解
        if (sysConfig.authExcludeMatch(path)
                || handlerMethod.hasMethodAnnotation(IgnoreLogin.class)
                || handlerMethod.getMethod().getDeclaringClass().isAnnotationPresent(IgnoreLogin.class)) {
            RequestUtil.setCurrentUser(CurrentUserInfo.ofNoUser());
            return true;
        }
        try{
            StpUtil.checkLogin();
            final SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            Preconditions.checkNotNull(tokenInfo, "请重新登录后再操作.");
            final CurrentUserInfo currentUserInfo = tokenStoreService.byToken(tokenInfo.tokenValue);
            RequestUtil.setCurrentUser(currentUserInfo);
            Preconditions.checkNotNull(currentUserInfo, "请重新登录后再操作.");
        }catch (Exception e){
            throw new RestException(RestCode.NO_USER_INFO, ExceptionUtil.getSimpleMsg(e))
                    .setData(sysConfig.getLoginUrl());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TokenUtil.clearHolder();
        RequestUtil.clearHolder();
    }
}
