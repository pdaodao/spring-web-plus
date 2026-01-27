package com.github.pdaodao.springwebplus.task.controller;

import com.github.pdaodao.springwebplus.base.auth.IgnoreLogin;
import com.github.pdaodao.springwebplus.task.service.ZtExecutorService;
import com.github.pdaodao.springwebplus.tool.task.CronTaskInfo;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 调度中心
 */
@Slf4j
@Hidden
@RestController
@RequestMapping("/executor/api/v1/admin")
@AllArgsConstructor
public class ZtExecutorAdminController {
    private final ZtExecutorService nodeService;
    @IgnoreLogin
    @PostMapping("callback")
    @Operation(summary = "任务回掉")
    public Boolean callback(@RequestBody CronTaskInfo taskInfo){
        return true;
    }
}