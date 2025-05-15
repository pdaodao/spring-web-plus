package com.github.pdaodao.springwebplus.tool.task.core;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskThreadPoolFactory {
    public static int bigCorePoolSize = 2;
    public static int bigMaxPoolSize = 100;
    public static int smallCorePoolSize = 2;
    public static int smallMaxPoolSize = 8;

    private static TaskThreadPool big;
    private static TaskThreadPool small;

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
}
