package com.github.pdaodao.springwebplus.bflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "bf_his_task_actor", autoResultMap = true)
@Schema(description = "流程历史任务参与者表")
public class BfHisTaskActorEntity extends AutoIdWithTimeEntity {

}
