package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.base.auth.IgnoreLogin;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.dao.SysRegionDao;
import com.github.pdaodao.springwebplus.entity.SysRegion;
import com.github.pdaodao.springwebplus.pojo.RegionLevel;
import com.github.pdaodao.springwebplus.tool.data.ListWrap;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "行政区划管理")
@RequestMapping(Constant.ApiPrefix + "/region")
@AllArgsConstructor
public class SysRegionController {
    private final SysRegionDao dao;

    @IgnoreLogin
    @GetMapping("list")
    @Operation(summary = "列表")
    public List<SysRegion> queryList(@Parameter(name = "title", description = "按名称检索") @RequestParam(required = false) final String title,
                                           @Parameter(name = "level", description = "类型") @RequestParam(required = false) final RegionLevel level,
                                           final PageRequestParam pageRequestParam) {
        PageHelper.startPage(pageRequestParam);
        final List<SysRegion> list = dao.list(QueryBuilder.lambda(SysRegion.class)
                .eq(SysRegion::getLevel, level)
                .like(title, SysRegion::getTitle, SysRegion::getId)
                .build());
        return list;
    }


    @IgnoreLogin
    @GetMapping("tree")
    @Operation(summary = "树")
    public List<SysRegion> tree() {
        final List<SysRegion> list = BeanUtils.copyToList(dao.all(), SysRegion.class);
        return IdUtil.toTree(list, SysRegion::getId, SysRegion::getPid);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public SysRegion save(@Validated @RequestBody SysRegion entity) {
        dao.save(entity);
        return entity;
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@RequestBody ListWrap<String> ids) {
        Preconditions.assertTrue(ids.empty(), "请指定主键");
        return dao.removeByIds(ids.getList());
    }

    @IgnoreLogin
    @GetMapping("detail")
    @Operation(summary = "详情")
    public SysRegion detail(@Parameter(name = "id", description = "id") final String id) {
        final SysRegion role = dao.getById(id);
        return role;
    }
}
