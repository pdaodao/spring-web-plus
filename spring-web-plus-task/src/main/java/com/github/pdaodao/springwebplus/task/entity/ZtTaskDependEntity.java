package com.github.pdaodao.springwebplus.task.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "任务依赖信息")
@TableName(value = "zt_task_depend", autoResultMap = true)
public class ZtTaskDependEntity extends SnowIdWithTimeEntity {
    @TableFieldIndex
    @Schema(description = "任务id")
    private String taskId;

    @TableFieldIndex
    @Schema(description = "下个任务id")
    private String nextTaskId;
}