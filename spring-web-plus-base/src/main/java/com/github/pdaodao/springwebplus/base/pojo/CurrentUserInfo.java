package com.github.pdaodao.springwebplus.base.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.Set;

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
    private Object tokenInfo;

    // 昵称
    private String name;

    // 角色id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long roleId;

    // 角色编码
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String roleName;

    // 角色名称
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String roleTitle;

    // 菜单列表
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object menus;

    // 无用户时 伪造一个
    public static CurrentUserInfo ofNoUser() {
        final CurrentUserInfo currentUserInfo = new CurrentUserInfo();
        currentUserInfo.setId(null);
        currentUserInfo.setUsername("");
        return currentUserInfo;
    }
}
