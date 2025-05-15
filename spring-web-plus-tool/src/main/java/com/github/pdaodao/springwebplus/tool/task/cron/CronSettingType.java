package com.github.pdaodao.springwebplus.tool.task.cron;

public enum CronSettingType {
    // 按年调度
    year,
    // 按月调度
    month,
    // 按周调度
    week,
    // 按天调度
    day,
    // 按小时调度
    hour,
    // 按分钟调度
    minute,
    // 按秒调度
    second,
    // 指定时间运行
    fixed,
    // 按CRON表达式
    cron
}
