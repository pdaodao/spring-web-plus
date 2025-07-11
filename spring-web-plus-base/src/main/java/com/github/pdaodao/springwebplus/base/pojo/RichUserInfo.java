package com.github.pdaodao.springwebplus.base.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class RichUserInfo extends CurrentUserInfo{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object roles;

    // 菜单列表
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object menus;
}
