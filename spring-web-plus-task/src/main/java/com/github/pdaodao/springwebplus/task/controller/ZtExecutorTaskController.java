package com.github.pdaodao.springwebplus.task.controller;

import com.github.pdaodao.springwebplus.base.auth.IgnoreLogin;
import com.github.pdaodao.springwebplus.base.auth.SysLogAop;
import com.github.pdaodao.springwebplus.task.service.ZtExecutorService;
import com.github.pdaodao.springwebplus.tool.task.CronTaskInfo;
import com.github.pdaodao.springwebplus.tool.task.LogResult;
import com.github.pdaodao.springwebplus.tool.task.LogUtil;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 执行节点
 */
@Slf4j
@Hidden
@RestController
@RequestMapping("/executor/api/v1/task")
@AllArgsConstructor
public class ZtExecutorTaskController {
    private final ZtExecutorService nodeService;
    @IgnoreLogin
    @PostMapping("submit")
    @Operation(summary = "提交任务")
    public Boolean submitTask(@RequestBody CronTaskInfo taskInfo){
        SysLogAop.getLog().ignore();
        System.out.println("task "+taskInfo.getTaskId()+" to node.");
        nodeService.doExecute(taskInfo);
        return true;
    }

    @IgnoreLogin
    @GetMapping("getLog")
    @Operation(summary = "获取任务运行日志")
    public LogResult getLog(final String logId, @RequestParam(defaultValue = "0") final Integer from){
        SysLogAop.getLog().ignore();
        final String logPath = LogUtil.getLogPath(DateTimeUtil.now(), logId);
        return LogUtil.readLog(logPath, from);
    }

}