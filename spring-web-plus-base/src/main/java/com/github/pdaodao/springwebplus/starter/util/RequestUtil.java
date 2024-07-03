package com.github.pdaodao.springwebplus.starter.util;

import com.github.pdaodao.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.starter.pojo.PageRequestParam;

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

    public static void clearHolder() {
        userHolder.remove();
        pageHolder.remove();
    }
}
