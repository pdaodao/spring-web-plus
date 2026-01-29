package com.github.pdaodao.springwebplus.tool.task;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.task.core.TaskRingThread;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 任务定时调度器
 */
@Slf4j
public class TaskTimer extends Thread{
    private final CronTaskLoader loader;
    private final TaskRingThread ringThread;
    private volatile boolean isRunning = true;
    public static final int tick = 1 * 1000;

    public TaskTimer(final TaskFactory executorFactory, final CronTaskLoader loader) {
        this.loader = loader;
        Preconditions.checkNotNull(executorFactory, "TaskExecutorFactory is null.");
        Preconditions.checkNotNull(loader, "TaskInfoLoader is null.");
        ringThread = new TaskRingThread(executorFactory);
        setName("PlusTaskTimer");
    }

    public void setIsRunning(final Boolean is){
        this.isRunning = is;
    }

    @Override
    public void run() {
        ringThread.start();
        try {
            TimeUnit.MILLISECONDS.sleep(1000 - System.currentTimeMillis() % 1000);
        } catch (Exception e) {
        }
        while (true){
            try{
               if(isRunning){
                   final List<CronTaskInfo> taskList = loader.load();
                   for(int i = 0; i < 60; i++){
                       scan(taskList);
                   }
               }else {
                   TimeUnit.MILLISECONDS.sleep(2000);
               }
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 扫描任务并调度
     * @throws Exception
     */
    private void scan(final List<CronTaskInfo> taskList) throws Exception{
        final long nowTime = DateTimeUtil.currentTimeMillis();
        final long upTime = nowTime + 1000;
        try{
            final List<CronTaskInfo> toUpdateList = new ArrayList<>();
            if(CollUtil.isNotEmpty(taskList)){
                for(final CronTaskInfo t: taskList){
                    if(t.getNextTime() == null){
                        continue;
                    }
                    if(t.getNextTime() <= upTime){
                        final boolean added = ringThread.addToRing(t, t.getNextTime());
                        if(added){
                            toUpdateList.add(t);
                        }
                    }
                }
            }
            for(final CronTaskInfo t: toUpdateList){
                if(t.getNextTime() == null){
                    continue;
                }
                final Date next = CronUtil.nextTime(t.getCronSetting(), new Date(t.getNextTime()));
                if(next == null){
                    t.setNextTime(null);
                }else{
                    t.setNextTime(next.getTime());
                }
                loader.setNext(t.getTaskId(), t.getNextTime());
            }
        }finally {
            final long t2 = DateTimeUtil.currentTimeMillis();
            if(t2 - nowTime < tick ){
                TimeUnit.MILLISECONDS.sleep(tick - System.currentTimeMillis() % tick);
            }
        }
    }
}