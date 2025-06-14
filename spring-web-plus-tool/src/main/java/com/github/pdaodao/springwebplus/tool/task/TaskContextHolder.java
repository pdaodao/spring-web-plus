package com.github.pdaodao.springwebplus.tool.task;

import org.slf4j.MDC;

public final class TaskContextHolder {

    public static final String TASK_ID = "taskId";
    public static final String TASK_WORK_SPACE = "taskWorkSpace";

    private TaskContextHolder() {}

    /**
     * 请求入口处，将任务日志meta信息写入上下文
     */
    public static void trace(Long taskId) {
        MDC.put(TASK_ID, String.valueOf(taskId));
    }

    /**
     * 清除任务日志meta信息上下文
     */
    public static void clear() {
        MDC.remove(TASK_ID);
    }
}