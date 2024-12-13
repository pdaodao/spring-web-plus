package com.github.pdaodao.springwebplus.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.auth.Permission;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.dao.SysDicDao;
import com.github.pdaodao.springwebplus.entity.SysDict;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "系统字典")
@RequestMapping(Constant.ApiPrefix + "/dic")
@AllArgsConstructor
public class SysDictController {
    private final SysDicDao dicDao;

    @GetMapping("tree")
    @Operation(summary = "字典树")
    public List<SysDict> tree(@Parameter(name = "name", description = "字典编码") @RequestParam(required = false) final String name) {
        if (StrUtil.isNotBlank(name)) {
            final SysDict dic = dicDao.dicByName(name);
            Preconditions.checkNotNull(dic, "字典{}不存在.", name);
            final List<SysDict> list = dicDao.listOrderBySeq(dic.getId());
            return IdUtil.toTree(list, SysDict::getId, SysDict::getPid);
        }
        final List<SysDict> list = dicDao.listOrderBySeq(null);
        return IdUtil.toTree(list, SysDict::getId, SysDict::getPid);
    }

    @GetMapping("/maps")
    @Operation(summary = "字典map结构")
    public Map<String, Map<String, String>> maps() {
        List<SysDict> list = dicDao.listOrderBySeq(null);
        list = IdUtil.toTree(list, SysDict::getId, SysDict::getPid);
        final Map<String, Map<String, String>> map = new LinkedHashMap<>();
        for (final SysDict dic : list) {
            final Map<String, String> sub = new LinkedHashMap<>();
            map.put(dic.getName(), sub);
            if (CollUtil.isEmpty(dic.getChildren())) {
                continue;
            }
            for (final SysDict subDic : dic.getChildren()) {
                sub.put(subDic.getName(), subDic.getTitle());
            }
        }
        return map;
    }

    @PostMapping("/save")
    @Operation(summary = "保存字典项")
    @Permission("sys:dict:save")
    public Boolean saveDic(@Valid @RequestBody SysDict dic) {
        dic.setPid("0");
        return dicDao.save(dic);
    }

    @PostMapping("/save-value")
    @Operation(summary = "保存字典值")
    @Permission("sys:dict:save")
    public Boolean saveDicValue(@Valid @RequestBody SysDict dic) {
        Preconditions.checkNotBlank(dic.getPid(), "请指定字典.");
        return dicDao.save(dic);
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除字典")
    @Permission("sys:dict:delete")
    public Boolean deleteSysDictType(@PathVariable("id") String id) {
        return dicDao.removeById(id);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "字典详情")
    @Permission("sys:dict:info")
    public SysDict getSysDictType(@PathVariable("id") String id) {
        return dicDao.getById(id);
    }

    @GetMapping("/list")
    @Operation(summary = "字典列表")
    @Permission("sys:dict:list")
    public List<SysDict> getSysDictTypeList() {
        return dicDao.listOrderBySeq("0");
    }

    @GetMapping("value/list")
    @Operation(summary = "字典值列表")
    @Permission("sys:dict-value:list")
    public List<SysDict> valueList(@Parameter(name = "name", description = "字典编码") final String name) {
        final SysDict dic = dicDao.dicByName(name);
        Preconditions.checkNotNull(dic, "字典{}不存在.", name);
        return IdUtil.toTree(dicDao.listOrderBySeq(dic.getId()), SysDict::getId, SysDict::getPid);
    }

    @GetMapping("value/map")
    @Operation(summary = "字典值map")
    @Permission("sys:dict-value:list")
    public Map<String, SysDict> valueMap(@Parameter(name = "name", description = "字典编码") final String name) {
        final SysDict dic = dicDao.dicByName(name);
        Preconditions.checkNotNull(dic, "字典{}不存在.", name);
        final List<SysDict> list = IdUtil.toTree(dicDao.listOrderBySeq(dic.getId()), SysDict::getId, SysDict::getPid);
        final Map<String, SysDict> map = new LinkedHashMap<>();
        for (final SysDict d : list) {
            if (StrUtil.isNotBlank(d.getName())) {
                map.put(d.getName(), d);
            }
        }
        return map;
    }
}
