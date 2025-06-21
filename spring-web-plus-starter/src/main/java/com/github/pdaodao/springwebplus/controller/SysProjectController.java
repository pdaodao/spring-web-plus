package com.github.pdaodao.springwebplus.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.auth.Permission;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.dao.SysProjectDao;
import com.github.pdaodao.springwebplus.entity.SysProject;
import com.github.pdaodao.springwebplus.tool.data.PageResult;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Hidden
@RestController
@Tag(name = "项目管理")
@RequestMapping(Constant.ApiPrefix + "/project")
@AllArgsConstructor
public class SysProjectController {
    private final SysProjectDao dao;
    @GetMapping("check-name")
    @Operation(summary = "编号是否存在")
    public Boolean checkNameExist(@Parameter(name = "name", description = "编码") final String name) {
        Preconditions.checkNotBlank(name, "请指定编码");
        final SysProject t = dao.byName(name);
        return ObjectUtil.isNotNull(t);
    }

    @GetMapping("check-title")
    @Operation(summary = "标题是否存在")
    public Boolean checkTitleExist(@Parameter(name = "title", description = "标题") final String title) {
        Preconditions.checkNotBlank(title, "请指定标题");
        final SysProject t = dao.byTitle(title);
        return ObjectUtil.isNotNull(t);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public SysProject save(@Validated @RequestBody SysProject entity) {
        dao.save(entity);
        return entity;
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    public SysProject info(@Parameter(name = "id", description = "主键") @RequestParam(required = false) final Long id,
                                @Parameter(name = "name", description = "编码") @RequestParam(required = false) final String name) {
        Preconditions.assertTrue(id == null && StrUtil.isBlank(name), "请指定主键或编码");
        SysProject info = null;
        if (StrUtil.isNotBlank(name)) {
            info = dao.byName(name);
        }else if(info == null && id != null){
            info = dao.byId(id);
        }
        return info;
    }

    @GetMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Parameter(name = "id", description = "主键") final Long id) {
        Preconditions.checkNotNull(id, "主键不能为空");
        return dao.removeById(id);
    }

    @GetMapping("list")
    @Operation(summary = "分页")
    @Permission("sys:project:list")
    public PageResult<SysProject> list(@Parameter(description = "按名称查询") @RequestParam(required = false) final String title,
                                            final PageRequestParam pageRequestParam) {
        try (final PageHelper pageHelper = PageHelper.startPage(pageRequestParam)) {
            final List<SysProject> list = dao.list(QueryBuilder
                    .lambda(SysProject.class)
                    .like(title, SysProject::getTitle, SysProject::getName)
                    .build());
            return pageHelper.toPageResult(list);
        }
    }
}
