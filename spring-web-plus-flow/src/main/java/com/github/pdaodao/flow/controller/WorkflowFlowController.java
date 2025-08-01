package com.github.pdaodao.flow.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.flow.query.WorkflowQuery;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.flow.entity.WorkflowDefine;
import com.github.pdaodao.flow.service.WorkflowInfoService;
import com.github.pdaodao.flow.util.Constants;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "流程定义")
@RequestMapping(Constants.WorkflowApiPrefix + "/flow")
@AllArgsConstructor
public class WorkflowFlowController {
    private final WorkflowInfoService infoService;

    @GetMapping("list")
    @Operation(summary = "流程列表")
    public List<WorkflowDefine> list(final WorkflowQuery query){
        PageHelper.startPage(query);
        return infoService.list(query);
    }

    @GetMapping("check-title")
    @Operation(summary = "名称是否存在")
    public Boolean checkTitleExist(@Validated WorkflowDefine flow) {
        flow.setTeamId(RequestUtil.getTeamOrDefault());
        final WorkflowDefine t = infoService.byTitle(flow.getTitle(), flow.getTeamId());
        return ObjectUtil.isNotNull(t);
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    public WorkflowDefine info(final String id, @RequestParam(required = false) final Integer version){
        return infoService.info(id, version);
    }

    @PostMapping("save-draft")
    @Operation(summary = "保存草稿")
    public WorkflowDefine saveDraft(@RequestBody @Validated final WorkflowDefine flow){
        return infoService.saveDraft(flow);
    }

    // 发布增加版本号
    @PostMapping("save-publish")
    @Operation(summary = "保存发布")
    public WorkflowDefine savePublish(@RequestBody @Validated final WorkflowDefine flow){
        return infoService.savePublish(flow);
    }
}