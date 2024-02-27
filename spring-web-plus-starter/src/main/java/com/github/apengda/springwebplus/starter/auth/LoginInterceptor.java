package com.github.apengda.springwebplus.starter.auth;

import com.github.apengda.springwebplus.starter.config.SysConfigProperties;
import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.apengda.springwebplus.starter.service.LoginService;
import com.github.apengda.springwebplus.starter.util.Preconditions;
import com.github.apengda.springwebplus.starter.util.RequestUtil;
import com.github.apengda.springwebplus.starter.util.TokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@AllArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    private final SysConfigProperties sysConfig;
    private final LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final String path = request.getServletPath();
        // 在配置的排除路径中
        if (sysConfig.authExcludeMatch(path)) {
            return true;
        }
        // 不需要验签
        if (handlerMethod.hasMethodAnnotation(IgnoreLogin.class)
                || handlerMethod.getMethod().getDeclaringClass().isAnnotationPresent(IgnoreLogin.class)) {
            return true;
        }
        // 用户信息
        final String token = TokenUtil.getToken(request);
        Preconditions.checkNotBlank(token, "请登录后再操作.");
        TokenUtil.setToken(token);
        final CurrentUserInfo currentUserInfo = loginService.byToken(token);
        Preconditions.checkNotNull(currentUserInfo, "登录已过期或登录信息不存在，请重新登录.");
        RequestUtil.setCurrentUser(currentUserInfo);
        // 权限判断
        final Permission permission = handlerMethod.getMethodAnnotation(Permission.class);
        return permission == null;
        // todo
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TokenUtil.clearHolder();
    }
}
