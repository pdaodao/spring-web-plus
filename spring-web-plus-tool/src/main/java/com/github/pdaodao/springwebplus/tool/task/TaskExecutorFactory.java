package com.github.pdaodao.springwebplus.tool.task;

public interface TaskExecutorFactory {
    TaskExecutor executor(final TaskInfo taskInfo);

    void triggerError(final TaskInfo taskInfo, final Exception exception);
}
