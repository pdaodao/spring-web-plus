package com.github.pdaodao.springwebplus.bflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@TableName(value = "bf_flow", autoResultMap = true)
@Schema(description = "流程定义表")
public class BfFlowEntity extends SnowIdWithTimeEntity {
    @Schema(description = "名称")
    private String title;


}