package com.github.pdaodao.springwebplus.tool.task.core;

public interface TaskRunnable {
    /**
     * 获取任务运行id
     * @return
     */
    String getId();

    /**
     * 任务运行开始 通知
     */
    default void start(){}

    /**
     * 任务执行内容
     * @throws Exception
     */
    void execute() throws Exception;

    /**
     * 任务运行结束 通知
     * @param e
     * @param costTimeMs
     */
    default void end(final Exception e, final long costTimeMs){}
}