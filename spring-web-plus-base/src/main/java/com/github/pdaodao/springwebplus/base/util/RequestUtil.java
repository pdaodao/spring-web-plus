package com.github.pdaodao.springwebplus.base.util;

import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {
    private static ThreadLocal<CurrentUserInfo> userHolder = new ThreadLocal<>();
    private static ThreadLocal<PageRequestParam> pageHolder = new ThreadLocal<>();

    public static String getUserId() {
        final CurrentUserInfo userInfo = getCurrentUser();
        Preconditions.checkNotNull(userInfo, "current-user-info is null.");
        return userInfo.getId();
    }

    public static String getUserNickname() {
        final CurrentUserInfo userInfo = getCurrentUser();
        Preconditions.checkNotNull(userInfo, "current-user-info is null.");
        return userInfo.getNickname();
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

    public static void clearHolder() {
        userHolder.remove();
        pageHolder.remove();
    }
}
