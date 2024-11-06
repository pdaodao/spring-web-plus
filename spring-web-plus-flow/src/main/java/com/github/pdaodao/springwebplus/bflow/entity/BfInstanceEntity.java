package com.github.pdaodao.springwebplus.bflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "bf_instance", autoResultMap = true)
@Schema(description = "流程实例表")
public class BfInstanceEntity extends SnowIdWithTimeEntity {
    @Schema(description = "流程定义id")
    @TableFieldIndex
    private String flowId;
}