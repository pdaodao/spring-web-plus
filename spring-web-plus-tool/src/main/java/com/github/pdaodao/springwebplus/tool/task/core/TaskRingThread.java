package com.github.pdaodao.springwebplus.tool.task.core;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.task.TaskFactory;
import com.github.pdaodao.springwebplus.tool.task.CronTaskInfo;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 任务调度 时间轮精细调度线程
 */
@Slf4j
public class TaskRingThread extends Thread{
    private volatile boolean isRunning = true;
    // 精确到100毫秒 调度任务
    private Map<Integer, List<CronTaskInfo>> ringData = new ConcurrentHashMap<>();

    private final TaskFactory executorFactory;

    public TaskRingThread(final TaskFactory factory){
        executorFactory = factory;
        setName("PlusTaskRingThread");
    }

    @Override
    public void run() {
        Preconditions.checkNotNull(executorFactory, "TaskRingThread TaskExecutorFactory is null.");
        while ((isRunning)){
            // 对准到100毫秒
            try {
                TimeUnit.MILLISECONDS.sleep(100 - System.currentTimeMillis() % 100);
            } catch (InterruptedException e) {
                if (isRunning) {
                    log.error(e.getMessage());
                }
            }
            try {
                final int tick = (int) ((DateTimeUtil.currentTimeMillis() % 60000) / 100);
                final List<CronTaskInfo> toRunList = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    final List<CronTaskInfo> tmpData = getRing((tick + 600 - i) % 600);
                    if (tmpData != null) {
                        toRunList.addAll(tmpData);
                    }
                }
                if (CollUtil.isNotEmpty(toRunList)) {
                    for (final CronTaskInfo task : toRunList) {
                        triggerTaskRun(task);
                    }
                }
            } catch (Exception e) {
                if (isRunning) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 触发任务执行 在这里不能消耗时间
     *
     * @param taskInfo
     */
    private void triggerTaskRun(final CronTaskInfo taskInfo) {
        if(taskInfo == null){
            return;
        }
        try{
            TaskThreadPoolFactory.ofSmall().execute(new TriggerTaskRunnable(taskInfo, executorFactory));
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 加入自旋 进行精细化控制
     * @param taskInfo
     * @param nextTime 一分钟之内的时间
     */
    public synchronized void addToRing(final CronTaskInfo taskInfo, long nextTime) {
        if(taskInfo == null || nextTime < 1000){
            return;
        }
        Preconditions.assertTrue(nextTime > DateTimeUtil.offsetMinute(DateTimeUtil.now(), 1).getTime(), "非法的精细时间调度要在一分钟之内");
        nextTime = nextTime % 60000;
        final int tick = (int) nextTime / 100;
        List<CronTaskInfo> list = ringData.get(tick);
        if (list == null) {
            list = new ArrayList<>();
            ringData.put(tick, list);
        }
        list.add(taskInfo);
    }

    /**
     * @param tick 一个间隔 为 100毫秒的 整数
     * @return
     */
    private synchronized List<CronTaskInfo> getRing(int tick) {
        return ringData.remove(tick);
    }

    public static class TriggerTaskRunnable implements TaskRunnable {
        private final CronTaskInfo taskInfo;
        private final TaskFactory executorFactory;

        public TriggerTaskRunnable(CronTaskInfo taskInfo, TaskFactory executorFactory) {
            this.taskInfo = taskInfo;
            this.executorFactory = executorFactory;
        }

        @Override
        public String getId() {
            return taskInfo.getTaskId();
        }

        @Override
        public void execute() throws Exception {
            try{
                final TaskRunnable taskExecutor = executorFactory.executor(taskInfo);
                Preconditions.checkNotNull(taskExecutor, "TaskExecutor is null by task-info");
                TaskThreadPoolFactory.ofBig().execute(taskExecutor);
            }catch (Exception e){
                executorFactory.triggerError(taskInfo, e);
            }
        }
    }
}
