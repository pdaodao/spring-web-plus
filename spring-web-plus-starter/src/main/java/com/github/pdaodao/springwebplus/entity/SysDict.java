package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithPid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据
 */
@Data
@TableName(value = "sys_dict", autoResultMap = true)
@Schema(description = "字典数据")
public class SysDict extends SnowIdWithTimeUserEntity implements WithPid<String> {
    @Schema(description = "父id")
    private String pid;

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

