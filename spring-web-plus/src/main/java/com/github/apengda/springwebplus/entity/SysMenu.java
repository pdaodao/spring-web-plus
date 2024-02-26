package com.github.apengda.springwebplus.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.github.apengda.springwebplus.starter.entity.AutoIdWithTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("sys_menu")
@Schema(description = "系统菜单")
public class SysMenu extends AutoIdWithTimeEntity {
    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "父id")
    private String pid;

    @Schema(description = "菜单类型，1：目录，2：菜单，3：权限")
    private Integer type;

    @Schema(description = "菜单编码")
    private String code;

    @Schema(description = "前端路由地址")
    private String routeUrl;

    @Schema(description = "重定向")
    private String routeRedirect;

    @Schema(description = "组件路径")
    private String componentPath;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "状态，0：禁用，1：启用")
    private Boolean enabled;

    @Schema(description = "是否显示,0：不显示，1：显示")
    private Boolean isShow;

    @Schema(description = "是否缓存，0：否 1：是")
    private Boolean isCache;
}
