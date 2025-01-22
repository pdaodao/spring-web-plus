package com.github.pdaodao.springwebplus.bflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "bf_his_task", autoResultMap = true)
@Schema(description = "流程任务历史表")
public class BfHisTaskEntity extends AutoIdWithTimeEntity {

}
