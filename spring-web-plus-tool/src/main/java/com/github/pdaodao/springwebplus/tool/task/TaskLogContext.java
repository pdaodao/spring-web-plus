package com.github.pdaodao.springwebplus.tool.task;

import lombok.Data;

import java.util.Date;

@Data
public class TaskLogContext {
    private String taskId;
    private String logId;
    private Date startTime;

    public static TaskLogContext of(final String taskId, final String logId, final Date startTime){
        final TaskLogContext ct = new TaskLogContext();
        ct.setTaskId(taskId);
        ct.setLogId(logId);
        ct.setStartTime(startTime);
        return ct;
    }
}