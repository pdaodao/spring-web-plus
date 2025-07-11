package com.github.pdaodao.springwebplus.entity;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.*;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@TableName(value = "sys_menu", autoResultMap = true)
@Schema(description = "系统菜单")
public class SysMenu extends BaseEntity implements WithChildren<SysMenu>, WithPidString {
    @Schema(description = "菜单编码")
    private String name;

    @Schema(description = "菜单名称")
    private String title;

    @Schema(description = "父id")
    private String pid;

    @Schema(description = "菜单类型，1：目录，2：菜单，3：权限")
    private Integer type;

    @Schema(description = "前端路由地址")
    private String path;

    @Schema(description = "权限字符串")
    private String permissions;

    @Schema(description = "重定向")
    private String routeRedirect;

    @Schema(description = "组件路径")
    private String componentPath;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "是否为外链")
    private String isFrame;

    @Schema(description = "状态，0：禁用，1：启用")
    private Boolean enabled;

    @Schema(description = "是否显示,0：不显示，1：显示")
    private Boolean isShow;

    @Schema(description = "是否缓存，0：否 1：是")
    private Boolean isCache;

    @Schema(description = "子项目")
    private transient List<SysMenu> children;

    public static void collectByIds(final Collection<SysMenu> menus, final Set<String> ids, final SysMenu root){
        if(CollUtil.isEmpty(menus)){
            return;
        }
        for(final SysMenu m: menus){
            final SysMenu menu = m.cloneIgnoreChildren();
            if(CollUtil.isNotEmpty(m.getChildren())){
                menu.setChildren(new ArrayList<>());
                collectByIds(m.getChildren(), ids, menu);
                if(CollUtil.isNotEmpty(menu.getChildren()) || ids.contains(m.getId())){
                    root.addChildren(menu);
                }
            }else if(ids.contains(m.getId())){
                root.addChildren(menu);
            }
        }
    }

    protected SysMenu cloneIgnoreChildren(){
        final SysMenu m = new SysMenu();
        BeanUtils.copyProperties(this, m, "children");
        return m;
    }

    protected void addChildren(final SysMenu ch){
        if(ch == null){
            return;
        }
        if(children == null){
            children = new ArrayList<>();
        }
        children.add(ch);
    }
}