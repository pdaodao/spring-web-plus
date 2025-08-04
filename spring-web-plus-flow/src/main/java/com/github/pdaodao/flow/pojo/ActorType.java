package com.github.pdaodao.flow.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "参与人类型")
public enum ActorType {
    @Schema(description = "指定用户")
    user,
    @Schema(description = "指定角色")
    role,
    @Schema(description = "指定部门")
    dept
}