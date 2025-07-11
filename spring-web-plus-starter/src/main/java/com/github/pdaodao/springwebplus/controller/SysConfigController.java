package com.github.pdaodao.springwebplus.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.springwebplus.base.auth.Permission;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.dao.SysConfigDao;
import com.github.pdaodao.springwebplus.entity.SysConfig;
import com.github.pdaodao.springwebplus.tool.data.ListWrap;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
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
@Tag(name = "系统参数配置")
@RequestMapping("/sys/api/v1/config")
@AllArgsConstructor
public class SysConfigController {
    private final SysConfigDao dao;

    @GetMapping("list")
    @Operation(summary = "分页")
    @Permission("sys:config:list")
    public List<SysConfig> list(@Parameter(description = "按关键词查询") @RequestParam(required = false) final String q,
                                final PageRequestParam pageRequestParam) {
        PageHelper.startPage(pageRequestParam);
        final List<SysConfig> list = dao.list(QueryBuilder
                    .lambda(SysConfig.class)
                    .like(q, SysConfig::getTitle, SysConfig::getConfigKey, SysConfig::getConfigValue)
                    .build());
        return list;
    }

    @GetMapping("check-key")
    @Operation(summary = "key是否存在")
    @Permission("sys:config:list")
    public Boolean checkKeyExist(@Parameter(name = "configKey", description = "key") final String configKey) {
        Preconditions.checkNotBlank(configKey, "请指定key");
        final SysConfig t = dao.byKey(configKey);
        return ObjectUtil.isNotNull(t);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @Permission("sys:config:save")
    public SysConfig save(@Validated @RequestBody SysConfig entity) {
        dao.save(entity);
        return entity;
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    @Permission("sys:config:list")
    public SysConfig info(@Parameter(name = "id", description = "主键") @RequestParam final String id) {
        return dao.getById(id);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @Permission("sys:config:save")
    public Boolean delete(@RequestBody ListWrap<String> ids) {
        Preconditions.assertTrue(ids.empty(), "主键不能为空");
        return dao.removeByIds(ids.getList());
    }
}