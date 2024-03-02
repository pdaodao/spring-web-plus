package com.github.apengda.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.apengda.springwebplus.starter.entity.SnowIdWithTimeUserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据
 */
@Data
@TableName(value = "sys_dict", autoResultMap = true)
@Schema(description = "字典数据")
public class SysDict extends SnowIdWithTimeUserEntity {
    @Schema(description = "字典值")
    private String value;

    @Schema(description = "字典名称")
    private String label;

    @Schema(description = "字典类型code")
    private String dictCode;

    @Schema(description = "状态 1：启用，0：禁用")
    private Boolean enabled;

    @Schema(description = "排序值")
    private Integer seq;

    @Schema(description = "备注")
    private String remark;
}

