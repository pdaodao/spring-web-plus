package com.github.pdaodao.springwebplus.tool.task;

import cn.hutool.core.util.IdUtil;
import com.github.pdaodao.springwebplus.tool.task.core.TaskRunnable;
import com.github.pdaodao.springwebplus.tool.task.core.TaskThreadPoolFactory;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 任务
 */
public abstract class TaskExecutor implements TaskRunnable, AutoCloseable {
    private String id;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private volatile Exception exception = null;
    private long lastExecuteTime = 0;
    private long lastCostTimeMs = 0;

    private Future future = null;

    @Override
    public void close() throws Exception {
        if(future != null){
            future.wait(5 * 60 * 1000);
        }
    }

    /**
     * 同步调用
     */
    public final void executeSync() throws Exception{
        final long t1 = DateTimeUtil.currentTimeMillis();
        if(id == null){
            id = IdUtil.fastUUID();
        }
        try{
            start();
            execute();
            end(null,  DateTimeUtil.currentTimeMillis() - t1);
        }catch (final Exception e){
            exception = e;
            end(e, DateTimeUtil.currentTimeMillis() - t1);
            throw e;
        }
    }


    /**
     * 异步调用
     * @return
     */
    public final Future executeAsync() {
        if(id == null){
            id = IdUtil.fastUUID();
        }
        future = TaskThreadPoolFactory.ofBig().execute(this);
        return future;
    }

    @Override
    public void start() {
        lastExecuteTime = DateTimeUtil.currentTimeMillis();
        isRunning.set(true);
    }

    @Override
    public void end(Exception e, long costTimeMs) {
        isRunning.set(false);
        exception = e;
        lastCostTimeMs = costTimeMs;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean getIsRunning() {
        return isRunning.get();
    }

    public Exception getException() {
        return exception;
    }

    public long getLastExecuteTime() {
        return lastExecuteTime;
    }

    public long getLastCostTimeMs() {
        return lastCostTimeMs;
    }
}