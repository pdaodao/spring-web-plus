package com.github.pdaodao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.flow.pojo.FlowStatus;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@TableName(value = "wf_task", autoResultMap = true)
@Schema(description = "流程实例任务")
public class WorkflowTask extends AutoIdWithTimeUserEntity {
    @TableFieldIndex
    @Schema(description = "流程实例id")
    private String instanceId;

    @Schema(description = "节点id")
    private String nodeId;

    @Schema(description = "节点标题")
    private String nodeTitle;

    @Schema(description = "审批状态")
    private FlowStatus flowStatus;

    @TableFieldIndex
    @Schema(description = "团队id")
    private String teamId;
}