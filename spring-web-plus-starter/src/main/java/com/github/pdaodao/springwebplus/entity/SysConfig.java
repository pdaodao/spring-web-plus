package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置
 */
@Data
@TableName(value = "sys_config", autoResultMap = true)
@Schema(description = "系统配置")
public class SysConfig extends BaseEntity {
    @Schema(description = "配置名称")
    private String title;

    @Schema(description = "配置key")
    @NotBlank(message = "key不能为空")
    private String configKey;

    @Schema(description = "配置值")
    @NotBlank(message = "值不能为空")
    private String configValue;

    @Schema(description = "是否是系统类型")
    private Boolean isSystem;

    @Schema(description = "备注")
    private String remark;
}
