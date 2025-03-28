package com.github.pdaodao.springwebplus.base.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 资源所有类型
 */
@Schema(description = "成员类型")
public enum MemberType {
    @Schema(description = "所有者")
    creator,
    @Schema(description = "管理者")
    manage,
    @Schema(description = "参与者")
    use
}
