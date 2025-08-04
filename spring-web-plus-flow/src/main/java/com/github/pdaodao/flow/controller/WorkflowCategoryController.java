package com.github.pdaodao.flow.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.flow.dao.WorkflowCategoryDao;
import com.github.pdaodao.flow.entity.WorkflowCategory;
import com.github.pdaodao.flow.util.Constants;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
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
@Tag(name = "流程分类")
@RequestMapping(Constants.WorkflowApiPrefix + "/category")
@AllArgsConstructor
public class WorkflowCategoryController {
    private final WorkflowCategoryDao categoryDao;

    @GetMapping("list")
    @Operation(summary = "流程列表")
    public List<WorkflowCategory> list(final PageRequestParam query){
        return categoryDao.list(RequestUtil.getTeamId(), query.getQ());
    }

    @GetMapping("check-title")
    @Operation(summary = "名称是否存在")
    public Boolean checkTitleExist(@Validated WorkflowCategory flow) {
        flow.setTeamId(RequestUtil.getTeamOrDefault());
        final WorkflowCategory t = categoryDao.byTitle(flow.getTitle(), flow.getTeamId());
        return ObjectUtil.isNotNull(t) && !ObjectUtil.equals(flow.getId(), t.getId());
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    public WorkflowCategory info(final String id){
        return categoryDao.getById(id);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@RequestBody IdWrap<String> idWrap){
        return categoryDao.removeById(idWrap.getId());
    }
}
