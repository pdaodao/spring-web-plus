package com.github.pdaodao.springwebplus.task.service;

import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.tool.task.CronTaskInfo;
import com.github.pdaodao.springwebplus.tool.task.TaskFactory;
import com.github.pdaodao.springwebplus.tool.task.core.TaskRunnable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 调度中心任务提交任务工厂
 */
@Slf4j
@Service
@AllArgsConstructor
public class ZtAdminTaskFactory implements TaskFactory {

    @Override
    public TaskRunnable executor(CronTaskInfo taskInfo) {
        return new ZtAdminTaskRunner(taskInfo, SpringUtil.getBean(ZtNodeService.class));
    }

    @Override
    public void triggerError(CronTaskInfo taskInfo, Exception exception) {
        log.error("task:"+taskInfo, exception);
    }

    /**
     * 调度中心任务： 把任务提交到执行器
     */
    public static class ZtAdminTaskRunner implements TaskRunnable{
        private final CronTaskInfo taskInfo;
        private final ZtNodeService nodeService;

        public ZtAdminTaskRunner(CronTaskInfo taskInfo, ZtNodeService nodeService) {
            this.taskInfo = taskInfo;
            this.nodeService = nodeService;
        }

        @Override
        public Long getId() {
            return taskInfo.getTaskId();
        }

        @Override
        public void execute() throws Exception {
            nodeService.triggerByAdmin(taskInfo);
        }
    }
}
