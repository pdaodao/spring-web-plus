package com.github.pdaodao.springwebplus.tool.task;

import com.github.pdaodao.springwebplus.tool.task.core.TaskRunnable;

/**
 * 任务生成工厂
 */
public interface TaskFactory {
    /**
     * 根据任务信息创建任务执行内容
     * @param taskInfo
     * @return
     */
    TaskRunnable executor(final CronTaskInfo taskInfo);

    /**
     * 任务触发阶段报错 如无法创建任务运行时
     * @param taskInfo
     * @param exception
     */
    void triggerError(final CronTaskInfo taskInfo, final Exception exception);
}