package com.github.apengda.springwebplus.starter.util;

import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;

public class RequestUtil {
    private static ThreadLocal<CurrentUserInfo> user = new ThreadLocal<>();

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
        return user.get();
    }

    public static void setCurrentUser(final CurrentUserInfo currentUser) {
        user.set(currentUser);
    }

}
