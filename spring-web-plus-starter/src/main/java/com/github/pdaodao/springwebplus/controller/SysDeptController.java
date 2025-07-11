package com.github.pdaodao.springwebplus.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.springwebplus.base.auth.Permission;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.dao.SysDeptDao;
import com.github.pdaodao.springwebplus.entity.SysDept;
import com.github.pdaodao.springwebplus.tool.data.ListWrap;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Tag(name = "部门管理")
@RequestMapping("/sys/api/v1/dept")
@AllArgsConstructor
public class SysDeptController {
    private final SysDeptDao deptDao;

    @GetMapping("tree")
    @Operation(summary = "树")
    @Permission("sys:dept:list")
    public List<SysDept> tree() {
        final List<SysDept> list = deptDao.list();
        return IdUtil.toTree(list, SysDept::getId, SysDept::getPid);
    }

    @GetMapping("check-title")
    @Operation(summary = "名称是否存在")
    @Permission("sys:dept:list")
    public Boolean checkTitleExist(@Validated  SysDept sysDept) {
        sysDept.setTeamId(RequestUtil.getTeamOrDefault());
        final SysDept t = deptDao.byTitle(sysDept.getTitle(), sysDept.getTeamId(), sysDept.getPid());
        return ObjectUtil.isNotNull(t);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    @Permission("sys:dept:save")
    public SysDept save(@Validated @RequestBody SysDept entity) {
        deptDao.save(entity);
        return entity;
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    @Permission("sys:dept:list")
    public SysDept info(@Parameter(name = "id", description = "主键") @RequestParam final String id) {
        return deptDao.getById(id);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    @Permission("sys:dept:save")
    public Boolean delete(@RequestBody ListWrap<String> ids) {
        Preconditions.assertTrue(ids.empty(), "主键不能为空");
        final List<SysDept> tree = tree();
        final Set<String> allIds = new HashSet<>();
        for(final String id: ids.getList()){
            final Set<String> subs = IdUtil.selfSubIds(tree, id, SysDept::getId, SysDept::getPid);
            if(subs != null){
                allIds.addAll(subs);
            }
        }
//        final long used = deviceDao.countByTypeIds(allIds);
//        Preconditions.assertTrue(used > 0, "使用中无法删除.");
        return deptDao.removeByIds(allIds);
    }
}
