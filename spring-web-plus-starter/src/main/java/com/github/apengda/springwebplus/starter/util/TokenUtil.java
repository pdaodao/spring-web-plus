package com.github.apengda.springwebplus.starter.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class TokenUtil {
    public static String TokenName = "Authorization";
    public static ThreadLocal<String> holder = new ThreadLocal<>();

    public static String getToken() {
        return holder.get();
    }

    public static void setToken(final String token) {
        holder.set(token);
    }

    public static void clearHolder() {
        holder.remove();
    }


    /**
     * 从请求中获取token
     *
     * @param request
     * @return
     */
    public static String getToken(final HttpServletRequest request) {
        Preconditions.checkNotNull(request, "request不能为空.");
        // 从请求头中获取token
        final String headToken = request.getHeader(TokenName);
        if (StrUtil.isNotBlank(headToken)) {
            return headToken;
        }
        // 从cookie中获取
        final Cookie cookie = ServletUtil.getCookie(request, TokenName);
        if (cookie != null && StrUtil.isNotBlank(cookie.getValue())) {
            return cookie.getValue();
        }
        // 从请求参数或获取
        return request.getParameter(TokenName);
    }
}
