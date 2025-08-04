package com.github.pdaodao.flow.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "参与者")
public class ActorUser {
    @Schema(description = "用户id/角色id/部门id")
    private String id;

    @Schema(description = "类型")
    private ActorType type;
}