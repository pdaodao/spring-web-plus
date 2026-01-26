package com.github.pdaodao.springwebplus.task.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.entity.WithPidString;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "任务运行指标监控")
@TableName(value = "zt_task_metric", autoResultMap = true)
public class ZtTaskMetricEntity extends SnowIdWithTimeEntity implements WithPidString {
    @TableFieldIndex
    @Schema(description = "任务运行id")
    private String logId;

    @Schema(description = "数据表名称")
    private String tableName;

    @Schema(description = "父记录id")
    @TableFieldSize(defaultValue = "0")
    private String pid;

    @Schema(description = "节点id")
    private String nodeId;

    @Schema(description = "读数据行数")
    @TableFieldSize(defaultValue = "0")
    private Long readCount;

    @Schema(description = "写数据行数")
    @TableFieldSize(defaultValue = "0")
    private Long writeCount;

    @Schema(description = "耗时(ms)")
    private Integer cost;
}