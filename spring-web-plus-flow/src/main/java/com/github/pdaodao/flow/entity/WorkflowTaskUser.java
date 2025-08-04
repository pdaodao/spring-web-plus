package com.github.pdaodao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.flow.pojo.FlowStatus;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


@Data
@TableName(value = "wf_task_user", autoResultMap = true)
@Schema(description = "流程实例任务人员")
public class WorkflowTaskUser extends SnowIdWithTimeUserEntity {
    @TableFieldIndex
    private String taskId;

    @Schema(description = "审批状态")
    private FlowStatus flowStatus;

    @Schema(description = "分配时间")
    private Date assignedTime;

    @Schema(description = "审批完成时间")
    private Date completedTime;

    // 比如 按角色审批 某用户审批时的 角色用户审批的id
    @Schema(description = "父id")
    private String pid;

    @TableFieldIndex
    @Schema(description = "用户id")
    private String userId;

    @TableFieldIndex
    @Schema(description = "角色id")
    private String roleId;

    @TableFieldIndex
    @Schema(description = "部门id")
    private String deptId;
}