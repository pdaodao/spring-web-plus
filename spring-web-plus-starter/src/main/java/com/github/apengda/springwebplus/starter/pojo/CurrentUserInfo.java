package com.github.apengda.springwebplus.starter.pojo;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CurrentUserInfo {
    // 主键
    private String id;
    // 登录用户名
    private String username;
    // 登录密码
    private String password;
    // 昵称
    private String nickname;
    // 角色编码
    private Set<String> roles;

    // 无用户时 伪造一个
    public static CurrentUserInfo ofNoUser() {
        final CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        currentUserInfo.setId(null);
        currentUserInfo.setUsername("");
        currentUserInfo.setNickname("匿名");
        return currentUserInfo;
    }
}
