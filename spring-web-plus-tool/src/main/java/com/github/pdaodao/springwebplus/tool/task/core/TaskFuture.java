package com.github.pdaodao.springwebplus.tool.task.core;

import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import lombok.Data;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class TaskFuture implements AutoCloseable{
    private final TaskRunnable taskRunnable;
    private Future future;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private volatile Exception exception = null;
    private long startExecuteTime = 0;
    private long endExecuteTime = 0;

    public TaskFuture(final TaskRunnable taskRunnable) {
        this.taskRunnable = taskRunnable;
        this.isRunning.set(true);
        this.startExecuteTime = DateTimeUtil.currentTimeMillis();
    }

    public static TaskFuture of(final TaskRunnable taskRunnable){
        return new TaskFuture(taskRunnable);
    }

    public void setRunning(final boolean is){
        isRunning.set(is);
    }

    public boolean getRunning(){
        return isRunning.get();
    }

    @Override
    public void close() throws Exception {
        if(future != null){
            future.wait(5 * 60 * 1000);
        }
    }
}
