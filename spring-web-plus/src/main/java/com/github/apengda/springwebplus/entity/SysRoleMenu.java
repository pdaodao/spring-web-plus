package com.github.apengda.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.apengda.springwebplus.starter.entity.SnowIdWithTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色菜单关系表
 */
@Data
@TableName(value = "sys_role_menu", autoResultMap = true)
@Schema(description = "角色菜单关系表")
public class SysRoleMenu extends SnowIdWithTimeEntity {
    @Schema(description = "角色id")
    private String roleId;

    @Schema(description = "菜单id")
    private String menuId;

    @Schema(description = "是否用户选中 0：否，1：是")
    private transient Boolean isChoice;
}
