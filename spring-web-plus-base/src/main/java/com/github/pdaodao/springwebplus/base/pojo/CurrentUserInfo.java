package com.github.pdaodao.springwebplus.base.pojo;

import lombok.Data;

@Data
public class CurrentUserInfo {
    // 主键
    private Long id;
    // 登录用户名
    private String username;
    // 用户昵称
    private String nickname;

    // 无用户时 伪造一个
    public static CurrentUserInfo ofNoUser() {
        final CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        currentUserInfo.setId(null);
        currentUserInfo.setUsername("");
        return currentUserInfo;
    }
}
