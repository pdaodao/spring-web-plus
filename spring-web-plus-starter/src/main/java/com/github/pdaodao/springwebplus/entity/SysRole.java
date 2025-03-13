package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 系统角色
 */
@Data
@TableName(value = "sys_role", autoResultMap = true)
@Schema(description = "系统角色")
public class SysRole extends SnowIdWithTimeUserEntity {
    @Schema(description = "角色唯一编码")
    private String name;

    @Schema(description = "角色名称")
    @Length(max = 32, message = "名称长度超过限制")
    private String title;

    @Schema(description = "是否系统内置角色 1：是，0：否")
    private Boolean isSystem;

    @Schema(description = "角色备注")
    @Length(max = 200, message = "备注长度超过限制")
    private String remark;
}