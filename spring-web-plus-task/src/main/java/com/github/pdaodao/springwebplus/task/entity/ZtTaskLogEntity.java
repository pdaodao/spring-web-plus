package com.github.pdaodao.springwebplus.task.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.tool.lang.ConfigOptions;
import com.github.pdaodao.springwebplus.tool.task.TaskStatus;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Data
@Schema(description = "任务运行记录")
@TableName(value = "zt_task_log", autoResultMap = true)
public class ZtTaskLogEntity extends AutoIdWithTimeEntity {
    @Schema(description = "任务id")
    private Long taskId;

    @Schema(description = "执行节点id")
    private Long nodeId;

    @Schema(description = "运行时参数")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ConfigOptions params;

    @Schema(description = "批次id")
    private Long batchId;

    @Schema(description = "是否是调度执行")
    @TableFieldSize(defaultValue = "0")
    private Boolean isCron;

    @Schema(description = "运行状态")
    private TaskStatus taskStatus;

    @Schema(description = "数据行数")
    private Long dataRows;

    @Schema(description = "耗时(ms)")
    private Integer cost;

    @Schema(description = "错误信息")
    private String error;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "任务结束时间")
    protected Date endTime;

    public void setError(String error) {
        this.error = StrUtils.cut(error, 300);
    }
}