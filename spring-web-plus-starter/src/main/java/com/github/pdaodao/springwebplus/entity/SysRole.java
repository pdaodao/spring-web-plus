package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import java.util.List;

/**
 * 系统角色
 */
@Data
@TableName(value = "sys_role", autoResultMap = true)
@Schema(description = "系统角色")
public class SysRole extends BaseEntity implements WithTeam {
    @Schema(description = "角色唯一编码,前端不用显示不用填写")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String name;

    @Schema(description = "角色名称")
    @Length(max = 32, message = "名称长度超过限制")
    private String title;

    @Schema(description = "是否系统内置角色 1：是，0：否")
    private Boolean isSystem;

    @Schema(description = "角色备注")
    @Length(max = 200, message = "备注长度超过限制")
    private String remark;

    @Schema(description = "团队id")
    private String teamId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "菜单ID集合")
    @TableField(exist = false)
    private List<String> menuIds;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "系统菜单")
    @TableField(exist = false)
    private List<SysMenu> menus;
}