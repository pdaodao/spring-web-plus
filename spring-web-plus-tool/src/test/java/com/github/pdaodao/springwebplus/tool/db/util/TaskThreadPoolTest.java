package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.thread.ThreadUtil;
import com.github.pdaodao.springwebplus.tool.task.TaskExecutor;
import com.github.pdaodao.springwebplus.tool.task.TaskExecutorFactory;
import com.github.pdaodao.springwebplus.tool.task.TaskInfo;
import com.github.pdaodao.springwebplus.tool.task.TaskTimer;
import com.github.pdaodao.springwebplus.tool.task.core.ArrayListTaskInfoLoader;
import com.github.pdaodao.springwebplus.tool.task.core.TaskRingThread;
import com.github.pdaodao.springwebplus.tool.task.cron.CronSetting;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;

import java.util.Date;

public class TaskThreadPoolTest {
    public static void main1(String[] args) throws Exception{
        final PrintTask p = new PrintTask("1");
        p.executeSync();
        System.out.println(p.getLastCostTimeMs());
        p.executeAsync();
        System.out.println("main");
        //ThreadUtil.sleep(2000);
        System.out.println("main-end");
    }

    public static void main2(String[] args) {
        final PrintFactory printFactory = new PrintFactory();
        final TaskRingThread ringThread = new TaskRingThread(printFactory);
        ringThread.start();
        for(int i = 1; i < 50; i++){
            final TaskInfo taskInfo = new TaskInfo();
            taskInfo.setTaskId(i+"");
            ringThread.addToRing(taskInfo, DateTimeUtil.offsetSecond(DateTimeUtil.now(), 3).getTime());
            ThreadUtil.sleep(2000);
        }
    }

    public static void main(String[] args) throws Exception{
        final PrintFactory printFactory = new PrintFactory();
        final ArrayListTaskInfoLoader loader = new ArrayListTaskInfoLoader();
        final TaskTimer taskTimer = new TaskTimer(printFactory, loader);
        taskTimer.start();
        final TaskInfo taskInfo = new TaskInfo();
        taskInfo.setTaskId("a1");
        taskInfo.setCronSetting(CronSetting.ofMinute(1));
        loader.add(taskInfo);
    }

    public static class PrintFactory implements TaskExecutorFactory {

        @Override
        public TaskExecutor executor(TaskInfo taskInfo) {
            return new PrintTask(taskInfo.getTaskId());
        }

        @Override
        public void triggerError(TaskInfo taskInfo, Exception exception) {

        }
    }

    public static class PrintTask extends TaskExecutor{
        private String id;

        public PrintTask(String id) {
            this.id = id;
        }

        @Override
        public void execute() throws Exception {
            ThreadUtil.sleep(3000);
            System.out.println(id + ":hello"+DateTimeUtil.formatDateTime(new Date()));
//            System.out.println(Thread.currentThread().getName()+":hello");
        }
    }
}
