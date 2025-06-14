package com.github.pdaodao.springwebplus.tool.task.core;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TaskThreadPoolFactory {
    public static int bigCorePoolSize = 2;
    public static int bigMaxPoolSize = 100;
    public static int smallCorePoolSize = 2;
    public static int smallMaxPoolSize = 8;

    private static TaskThreadPool big;
    private static TaskThreadPool small;

    public static final Map<Long, TaskFuture> taskFutureMap = new ConcurrentHashMap<>();

    public static synchronized TaskThreadPool ofBig(){
        if(big == null){
            big = new TaskThreadPool(bigCorePoolSize, bigMaxPoolSize);
        }
        return big;
    }

    public static synchronized TaskThreadPool ofSmall(){
        if(small == null){
            small = new TaskThreadPool(smallCorePoolSize, smallMaxPoolSize);
        }
        return small;
    }

    public void shutdown(){
        if(small != null){
            try{
                small.shutdown();
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }
        if(big != null){
            big.shutdown();
        }
    }


    public static void put(final Long id, final TaskFuture taskExecutor){
        if(id == null || taskExecutor == null){
            return;
        }
       // Preconditions.assertTrue(taskMap.containsKey(id), "duplicated task to run.");
        taskFutureMap.put(id, taskExecutor);
    }

    public static TaskFuture get(final String id){
        if(StrUtil.isBlank(id)){
            return null;
        }
        return taskFutureMap.get(id);
    }

    public static void remove(final Long id){
        if(id != null){
            taskFutureMap.remove(id);
        }
    }
}
