package com.github.pdaodao.springwebplus.tool.task.core;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.task.TaskExecutor;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 任务执行线程池 所有的任务线程由这里提供
 */
public class TaskThreadPool {
    public final Map<String, TaskExecutor> taskMap = new ConcurrentHashMap<>();

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
    public Future execute(final TaskRunnable taskRunnable){
        Preconditions.checkNotNull(taskRunnable, "TaskExecutorCenter Runnable task is null.");
        return taskExecutor.submit(() -> {
            final long t1 = DateTimeUtil.currentTimeMillis();
            try{
                taskRunnable.start();
                taskRunnable.execute();
                taskRunnable.end(null, DateTimeUtil.currentTimeMillis() - t1);
            }catch (Exception e){
                taskRunnable.end(e, DateTimeUtil.currentTimeMillis() - t1);
            }
        });
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

    public void put(final String id, final TaskExecutor taskExecutor){
        if(StrUtil.isBlank(id) || taskExecutor == null){
            return;
        }
        Preconditions.assertTrue(taskMap.containsKey(id), "duplicated task to run.");
        taskMap.put(id, taskExecutor);
    }

    public void remove(final String id){
        taskMap.remove(id);
    }
}