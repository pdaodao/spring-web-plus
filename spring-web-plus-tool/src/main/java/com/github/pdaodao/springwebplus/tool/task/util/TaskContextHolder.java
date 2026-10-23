package com.github.pdaodao.springwebplus.tool.task.util;

import java.time.LocalDateTime;

public class TaskContextHolder {
    private static final ThreadLocal<TaskContext> context = new ThreadLocal<>();

    public static TaskContext of(final String taskId, final String logId, final LocalDateTime startTime) {
        final TaskContext ct = TaskContext.of(taskId, logId, startTime);
        context.set(ct);
        return ct;
    }

    public static TaskContext get(){
        TaskContext ct = context.get();
        if(ct == null){
            ct = new TaskContext();
            context.set(ct);
        }
        return ct;
    }

    public static void set(final TaskContext ct){
        context.set(ct);
    }

    public static void clean(){
        context.remove();
    }
}
