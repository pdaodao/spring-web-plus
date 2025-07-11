package com.github.pdaodao.springwebplus.query;

import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysUserQuery extends PageRequestParam {
    @Schema(description = "用户登录名")
    private String username;

    @Schema(description = "id")
    private String id;

    @Schema(description = "角色id")
    private String roleId;
}
