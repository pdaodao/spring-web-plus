package com.github.pdaodao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.pdaodao.flow.pojo.FlowStatus;
import com.github.pdaodao.flow.pojo.TaskPerformType;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.tool.data.TableRowData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * task 表只描述任务本身，不存储具体审批人
 */
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

    @Schema(description = "参与类型,普通、会签")
    private TaskPerformType performType;

    @TableFieldIndex
    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "审批到期时间")
    private LocalDateTime dueDate;

    @TableFieldSize(5000)
    @Schema(description = "表单数据")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private TableRowData variables;
}