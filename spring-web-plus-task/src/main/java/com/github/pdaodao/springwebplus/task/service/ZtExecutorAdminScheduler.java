package com.github.pdaodao.springwebplus.task.service;

import com.github.pdaodao.springwebplus.tool.task.TaskTimer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class ZtExecutorAdminScheduler implements InitializingBean {
    private final ZtAdminTaskFactory adminTaskFactory;
    private final ZtCronTaskInfoLoader taskInfoLoader;
    private TaskTimer taskTimer;

    public ZtExecutorAdminScheduler(ZtCronTaskInfoLoader taskInfoLoader) {
        this.adminTaskFactory = new ZtAdminTaskFactory();
        this.taskInfoLoader = taskInfoLoader;
    }

    public void setIsAdmin(final boolean is){
        taskTimer.setIsRunning(is);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        taskTimer = new TaskTimer(adminTaskFactory, taskInfoLoader);
        taskTimer.setIsRunning(false);
        taskTimer.start();
    }
}
