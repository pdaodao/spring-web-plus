package com.github.apengda.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.apengda.springwebplus.starter.entity.AutoIdWithTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统角色
 */
@Data
@TableName("sys_role")
@Schema(description = "系统角色")
public class SysRole extends AutoIdWithTimeEntity {
    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色唯一编码")
    private String code;

    @Schema(description = "是否系统内置角色 1：是，0：否")
    private Boolean isSystem;

    @Schema(description = "角色备注")
    private String remark;

    @Schema(description = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createId;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateId;

    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

}