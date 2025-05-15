package com.github.pdaodao.springwebplus.tool.task;

import com.github.pdaodao.springwebplus.tool.task.core.TaskRunnable;

public interface TaskFactory {
    TaskRunnable executor(final TaskInfo taskInfo);

    /**
     * 任务触发阶段报错 如无法创建任务运行时
     * @param taskInfo
     * @param exception
     */
    void triggerError(final TaskInfo taskInfo, final Exception exception);
}