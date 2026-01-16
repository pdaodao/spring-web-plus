package com.github.pdaodao.springwebplus.task.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.pdaodao.springwebplus.base.entity.*;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.tool.task.TaskStatus;
import com.github.pdaodao.springwebplus.tool.task.cron.CronSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "任务调度信息")
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName(value = "zt_task_info", autoResultMap = true)
public class ZtTaskInfoEntity extends SnowIdWithTimeEntity implements WithPidString, WithTeam, WithDelete {
    @TableFieldSize(200)
    @NotBlank(message = "名称不能为空")
    @Length(max = 100, message = "名称不能超过100个字")
    private String title;

    @Schema(description = "命名空间")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String namespace;

    @Schema(description = "任务类型")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String taskType;

    @Schema(description = "父分类id")
    private String pid;

    @Schema(description = "是否是分类")
    @TableFieldSize(defaultValue = "0")
    private Boolean isDir;

    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String teamId;

    @TableFieldSize(3000)
    @TableField(typeHandler = JacksonTypeHandler.class)
    private CronSetting cronSetting;

    @Schema(description = "下次执行时间")
    @TableField(updateStrategy =  FieldStrategy.NEVER)
    private Long nextTime;

    @Schema(description = "是否启用调度")
    private Boolean enabled;

    @Schema(description = "运行状态")
    private TaskStatus taskStatus;

    @Schema(description = "最后运行的日志id")
    private Long logId;

    @Schema(description = "版本")
    private Integer version;

    @TableLogic
    private Boolean isDeleted;
}