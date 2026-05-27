package com.github.pdaodao.springwebplus.tool.task.core;

import cn.hutool.core.thread.NamedThreadFactory;
import com.github.pdaodao.springwebplus.tool.task.LogUtil;
import com.github.pdaodao.springwebplus.tool.task.TaskLogContext;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.*;

/**
 * 任务执行线程池 所有的任务线程由这里提供
 */
public class TaskThreadPool {
    public ExecutorService taskExecutor;

    public TaskThreadPool(int corePoolSize, int maxPoolSize) {
        taskExecutor = new ThreadPoolExecutor(corePoolSize,
                maxPoolSize, 60L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(corePoolSize),
                new NamedThreadFactory("plus-task", false));
    }

    /**
     * 执行
     * @param taskRunnable
     * @return
     */
    public TaskFuture execute(final TaskRunnable taskRunnable){
        Preconditions.checkNotNull(taskRunnable, "TaskExecutorCenter Runnable task is null.");
        final TaskFuture taskFuture = TaskFuture.of(taskRunnable);
        TaskThreadPoolFactory.put(taskRunnable.getId(), taskFuture);
        final Future future = taskExecutor.submit(() -> {
            LocalDateTime startTime = Instant.ofEpochMilli(taskFuture.getStartExecuteTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            LogUtil.setContext(null, taskRunnable.getId(), startTime);
            try{
                taskRunnable.start();
                taskRunnable.execute();
                taskFuture.setEndExecuteTime(DateTimeUtil.currentTimeMillis());
                taskRunnable.end(null, taskFuture.getEndExecuteTime() - taskFuture.getStartExecuteTime());
            }catch (Exception e){
                taskFuture.setEndExecuteTime(DateTimeUtil.currentTimeMillis());
                taskFuture.setException(e);
                taskRunnable.end(null, taskFuture.getEndExecuteTime() - taskFuture.getStartExecuteTime());
            }finally {
                TaskThreadPoolFactory.remove(taskRunnable.getId());
                LogUtil.clear();
            }
        });
        taskFuture.setFuture(future);
        return  taskFuture;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException{
        return taskExecutor.awaitTermination(timeout, unit);
    }

    public void shutdown(){
        taskExecutor.shutdown();
    }

    public List<Runnable> shutdownNow(){
        return taskExecutor.shutdownNow();
    }
}