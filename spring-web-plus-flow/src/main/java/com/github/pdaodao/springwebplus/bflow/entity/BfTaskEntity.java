package com.github.pdaodao.springwebplus.bflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeEntity;
import com.github.pdaodao.springwebplus.bflow.pojo.PerformType;
import com.github.pdaodao.springwebplus.bflow.pojo.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "bf_task", autoResultMap = true)
@Schema(description = "流程任务表")
public class BfTaskEntity extends AutoIdWithTimeEntity {
    @Schema(description = "流程实例id")
    private Long instanceId;

    @Schema(description = "父任务id")
    private Long parentTaskId;

    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务类型")
    private TaskType taskType;

    @Schema(description = "参与方式")
    private PerformType performType;

    @Schema(description = "任务关联的表单url")
    protected String actionUrl;

    @Schema(description = "变量json")
    protected String variable;

    @Schema(description = "委托人ID")
    protected Long assignorId;

    @Schema(description = "委托人")
    protected String assignor;

    @Schema(description = "期望任务完成时间")
    protected Date expireTime;

    @Schema(description = "提醒时间")
    protected Date remindTime;

    @Schema(description = "提醒次数")
    protected Integer remindRepeat;

    @Schema(description = "是否已阅")
    protected Boolean viewed;
}
