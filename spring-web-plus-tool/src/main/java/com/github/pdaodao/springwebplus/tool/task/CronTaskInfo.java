package com.github.pdaodao.springwebplus.tool.task;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.lang.ConfigOptions;
import com.github.pdaodao.springwebplus.tool.task.cron.CronSetting;
import lombok.Data;

@Data
public class CronTaskInfo {
    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务运行id
     */
    private String logId;

    /**
     * 任务类型
     */
    private String taskType;

    private Boolean isCron = true;

    /**
     * 定时信息
     */
    private CronSetting cronSetting;

    /**
     * 下次运行时间
     */
    private Long nextTime;

    /**
     * 运行参数
     */
    private ConfigOptions params;

    public String key(){
        return StrUtil.join(",", taskId, logId);
    }
}
