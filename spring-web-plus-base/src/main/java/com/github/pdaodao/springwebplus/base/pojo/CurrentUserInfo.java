package com.github.pdaodao.springwebplus.base.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class CurrentUserInfo {
    // 主键
    private Long id;
    // 登录用户名
    private String username;

    // 头像
    private String avatar;

    // token
    private String token;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TokenInfo tokenInfo;

    // 昵称
    private String name;

    // 无用户时 伪造一个
    public static CurrentUserInfo ofNoUser() {
        final CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        currentUserInfo.setId(null);
        currentUserInfo.setUsername("");
        return currentUserInfo;
    }
}
