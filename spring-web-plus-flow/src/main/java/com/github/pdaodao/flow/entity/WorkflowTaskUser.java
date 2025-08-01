package com.github.pdaodao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@TableName(value = "wf_task_user", autoResultMap = true)
@Schema(description = "流程实例任务人员")
public class WorkflowTaskUser extends AutoIdWithTimeUserEntity {
    @TableFieldIndex
    @Schema(description = "应用id")
    private Long appId;
}
