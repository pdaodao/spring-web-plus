package com.github.pdaodao.springwebplus.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.entity.WithChildren;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "sys_menu", autoResultMap = true)
@Schema(description = "系统菜单")
public class SysMenu extends AutoIdWithTimeEntity implements WithChildren<SysMenu> {
    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "父id")
    private Long pid;

    @Schema(description = "菜单类型，1：目录，2：菜单，3：权限")
    private Integer type;

    @Schema(description = "菜单编码")
    private String idCode;

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

    @Schema(description = "子项目")
    private transient List<SysMenu> children;
}
