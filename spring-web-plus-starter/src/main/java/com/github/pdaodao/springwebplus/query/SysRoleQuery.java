package com.github.pdaodao.springwebplus.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysRoleQuery {
    @Schema(description = "是否系统内置角色 1：是，0：否")
    private Boolean isSystem;
}
