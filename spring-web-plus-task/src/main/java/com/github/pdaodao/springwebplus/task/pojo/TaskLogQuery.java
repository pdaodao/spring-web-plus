package com.github.pdaodao.springwebplus.task.pojo;

import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.tool.task.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TaskLogQuery extends PageRequestParam {
    @Schema(description = "任务id")
    private Long taskId;

    @Schema(description = "命名空间")
    private String namespace;

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "运行状态")
    private TaskStatus taskStatus;

    @Schema(hidden = true)
    private Long teamId;
}
