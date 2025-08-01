package com.github.pdaodao.flow.controller;

import com.github.pdaodao.flow.entity.WorkflowInstance;
import com.github.pdaodao.flow.pojo.FlowFormData;
import com.github.pdaodao.flow.service.WorkflowRuntimeService;
import com.github.pdaodao.flow.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "审批流运行时")
@RequestMapping(Constants.WorkflowApiPrefix + "/runtime")
@AllArgsConstructor
public class WorkflowRuntimeController {
    private final WorkflowRuntimeService runtimeService;

    @PostMapping("start")
    @Operation(summary = "开启流程")
    public WorkflowInstance startProcess(@RequestBody FlowFormData formData) {
        return runtimeService.start(formData);
    }

    @PostMapping("/tasks/{taskId}/complete")
    @Operation(summary = "审批")
    public WorkflowInstance taskComplete() {
        return null;
    }

    @GetMapping("tasks")
    @Operation(summary = "当前用户的待办任务")
    public void currentTodoTask(){
        
    }
}
