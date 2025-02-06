package com.github.pdaodao.springwebplus.base.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class RichUserInfo extends CurrentUserInfo{
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
}
