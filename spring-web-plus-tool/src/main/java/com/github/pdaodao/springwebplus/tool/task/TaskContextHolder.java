package com.github.pdaodao.springwebplus.tool.task;

import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import org.slf4j.MDC;

import java.io.File;
import java.io.FilterOutputStream;
import java.util.Date;

public final class TaskContextHolder {

    public static final String TASK_ID = "taskId";
    public static final String TASK_WORK_SPACE = "taskWorkSpace";

    private TaskContextHolder() {}

    /**
     * 请求入口处，将任务日志meta信息写入上下文
     */
    public static void trace(String taskId) {
        MDC.put("logSubPath", logSubPath(new Date(), taskId));
        MDC.put(TASK_ID, String.valueOf(taskId));
    }

    public static String logSubPath(Date d, String taskId){
        if(d == null){
            d = new Date();
        }
        final String logSubPath = DateTimeUtil.formatYearMonth(d) + File.separator + "task-" + taskId + ".log";
        return logSubPath;
    }

    /**
     * 清除任务日志meta信息上下文
     */
    public static void clear() {
        MDC.clear();
    }
}