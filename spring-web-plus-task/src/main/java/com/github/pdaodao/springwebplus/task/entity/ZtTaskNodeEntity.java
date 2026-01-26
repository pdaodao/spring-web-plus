package com.github.pdaodao.springwebplus.task.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.entity.WithEnabled;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "任务运行服务器节点")
@TableName(value = "zt_task_node", autoResultMap = true)
public class ZtTaskNodeEntity extends SnowIdWithTimeEntity implements WithEnabled {
    // host:port
    @Schema(description = "地址")
    private String url;

    @Schema(description = "是否是调度中心")
    @TableFieldSize(defaultValue = "true")
    private Boolean isAdmin;

    @Schema(description = "是否是执行器")
    @TableFieldSize(defaultValue = "true")
    private Boolean isExecutor;

    @Schema(description = "提交请求校验码")
    private String access;

    @Schema(description = "是否启用")
    @TableFieldSize(defaultValue = "true")
    private Boolean enabled;
}