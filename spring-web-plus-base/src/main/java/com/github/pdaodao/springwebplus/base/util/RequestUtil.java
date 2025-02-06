package com.github.pdaodao.springwebplus.base.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpUtil;
import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestUtil {
    private static ThreadLocal<CurrentUserInfo> userHolder = new ThreadLocal<>();
    private static ThreadLocal<PageRequestParam> pageHolder = new ThreadLocal<>();

    public static Long getUserId() {
        final CurrentUserInfo userInfo = getCurrentUser();
        if (userInfo == null) {
            return null;
        }
        Preconditions.checkNotNull(userInfo, "current-user-info is null.");
        return userInfo.getId();
    }

    public static String getUserNickname() {
        final CurrentUserInfo userInfo = getCurrentUser();
        if (userInfo == null) {
            return null;
        }
        Preconditions.checkNotNull(userInfo, "current-user-info is null.");
        return userInfo.getName();
    }


    public static CurrentUserInfo getCurrentUser() {
        return userHolder.get();
    }

    public static void setCurrentUser(final CurrentUserInfo currentUser) {
        userHolder.set(currentUser);
    }

    public static PageRequestParam getPageParam() {
        final PageRequestParam pp = pageHolder.get();
        pageHolder.remove();
        return pp;
    }

    public static void setPageParam(final PageRequestParam pp) {
        pageHolder.set(pp);
    }

    public static String getFromHead(String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        final HttpServletRequest httpServletRequest = getRequest();
        if (httpServletRequest == null) {
            return null;
        }
        return getFromHeader(httpServletRequest, name);
    }

    public static String getFromCookie(final String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        final HttpServletRequest httpServletRequest = getRequest();
        if (httpServletRequest == null) {
            return null;
        }
        final Cookie[] cks = httpServletRequest.getCookies();
        if(cks == null){
            return null;
        }
        for(final Cookie ck: cks){
            if(equalsIgnoreLine(ck.getName(), name)){
                return StrUtil.trim(ck.getValue());
            }
        }
        return null;
    }


    public static String getFromHeader(HttpServletRequest httpRequest, String head) {
        final Enumeration<String> names = httpRequest.getHeaderNames();
        while (names.hasMoreElements()) {
            final String h = names.nextElement();
            if (equalsIgnoreLine(h, head)) {
                head = h;
                break;
            }
        }
        return httpRequest.getHeader(head);
    }

    private static boolean equalsIgnoreLine(String h, String head) {
        if (StrUtil.isBlank(h) | StrUtil.isBlank(head)) {
            return false;
        }
        h = h.trim().toLowerCase().replaceAll("-", "").replaceAll("_", "");
        head = head.trim().toLowerCase().replaceAll("-", "").replaceAll("_", "");
        return h.equals(head);
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        final ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attrs;
        return servletRequestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        final HttpServletResponse response = attributes.getResponse();
        return response;
    }



    public void addCookie(final String name, final String tokenValue, long cookieTimeout){
        // todo
//        final SaCookieConfig cfg = saTokenConfig().getCookie();
//        if(cookieTimeout >= 0 && cookieTimeout <= 3){
//            cookieTimeout = saTokenConfig().getTimeout();
//        }
//        final SaCookie cookie = new SaCookie()
//                .setName(name)
//                .setValue(tokenValue)
//                .setMaxAge((int) cookieTimeout)
//                .setDomain(cfg.getDomain())
//                .setPath(cfg.getPath())
//                .setSecure(cfg.getSecure())
//                .setHttpOnly(cfg.getHttpOnly())
//                .setSameSite(cfg.getSameSite());
//        SaHolder.getResponse().addCookie(cookie);
    }

    public static void sendRedirect(final String redirect) throws IOException {
        Preconditions.checkNotBlank(redirect, "重定向地址不能为空");
        getResponse().sendRedirect(redirect);
    }

    public static void sendRedirect(final String redirect, final Map<String, ?> params) throws IOException {
        Preconditions.checkNotBlank(redirect, "重定向地址不能为空");
        String r = redirect;
        if (MapUtil.isNotEmpty(params)) {
            int hashIndex = redirect.indexOf("#");
            String p1 = redirect;
            String p2 = "";
            if(hashIndex > 0){
                final String[] sp = redirect.split("#");
                p1 = sp[0];
                p2 = "#"+sp[1];
            }
            int qIndex = p1.indexOf("?");
            String sp = "?";
            if(qIndex > 0){
                sp = "&";
            }
            r = p1 + sp + HttpUtil.toParams(params) + p2;
        }
        getResponse().sendRedirect(r);
    }

    public static void main(String[] args) throws Exception{
        final String r = "http://127.0.0.1#index";
        final Map<String, String> map = new HashMap<>();
        map.put("q", "123");
        sendRedirect(r, map);
    }

    public static void clearHolder() {
        userHolder.remove();
    }
}
