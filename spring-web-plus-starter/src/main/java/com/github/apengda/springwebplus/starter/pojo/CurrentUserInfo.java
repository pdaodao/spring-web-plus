package com.github.apengda.springwebplus.starter.pojo;

import lombok.Data;

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
}
