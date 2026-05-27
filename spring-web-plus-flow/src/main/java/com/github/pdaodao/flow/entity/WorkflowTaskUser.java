package com.github.pdaodao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.flow.pojo.FlowStatus;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.base.pojo.handler.FileInfoListTypeHandler;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@TableName(value = "wf_task_user", autoResultMap = true)
@Schema(description = "流程实例任务人员")
public class WorkflowTaskUser extends SnowIdWithTimeUserEntity {
    @TableFieldIndex
    @Schema(description = "任务表id")
    private String taskId;

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

    @Schema(description = "审批状态")
    private FlowStatus flowStatus;

    @Schema(description = "分配时间")
    private LocalDateTime assignedTime;

    @Schema(description = "审批完成时间")
    private LocalDateTime completedTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否抄送")
    @TableFieldSize(defaultValue = "false")
    private Boolean isCc;

    @Schema(description = "是否通知")
    @TableFieldSize(defaultValue = "false")
    private Boolean notified;

    @Schema(description = "通知时间")
    private LocalDateTime notifyTime;

    @TableFieldSize(5000)
    @Schema(description = "附件")
    @TableField(typeHandler = FileInfoListTypeHandler.class)
    private List<FileInfo> files;
}