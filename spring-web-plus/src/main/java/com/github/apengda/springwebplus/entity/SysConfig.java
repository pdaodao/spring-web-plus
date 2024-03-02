package com.github.apengda.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.apengda.springwebplus.starter.entity.AutoIdWithTimeUserEntity;
import com.github.apengda.springwebplus.starter.entity.SnowIdWithTimeUserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统配置
 */
@Data
@TableName("sys_config")
@Schema(description = "系统配置")
public class SysConfig extends SnowIdWithTimeUserEntity {
    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置key")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "是否是系统字典类型")
    private Boolean isSystem;

    @Schema(description = "状态 1：正常，0：禁用")
    private Boolean status;

    @Schema(description = "备注")
    private String remark;
}
