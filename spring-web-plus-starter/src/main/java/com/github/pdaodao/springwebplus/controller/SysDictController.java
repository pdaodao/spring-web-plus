package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.base.auth.Permission;
import com.github.pdaodao.springwebplus.dao.SysDicDao;
import com.github.pdaodao.springwebplus.entity.SysDict;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "系统字典")
@RequestMapping(Constant.ApiPrefix + "/dic")
@AllArgsConstructor
public class SysDictController {
    private final SysDicDao dicDao;

    @PostMapping("/add")
    @Operation(summary = "添加字典类型")
    @Permission("sys:dict:add")
    public Boolean addSysDictType(@Valid @RequestBody SysDict dic) {
        return dicDao.save(dic);
    }

    @PostMapping("/update")
    @Operation(summary = "修改字典类型")
    @Permission("sys:dict:update")
    public Boolean updateSysDictType(@Valid @RequestBody SysDict dic) {
        return dicDao.save(dic);
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除字典类型")
    @Permission("sys:dict:delete")
    public Boolean deleteSysDictType(@PathVariable String id) {
        return dicDao.removeById(id);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "字典详情")
    @Permission("sys:dict:info")
    public SysDict getSysDictType(@PathVariable String id) {
        return dicDao.getById(id);
    }

    @GetMapping("/list")
    @Operation(summary = "字典列表")
    @Permission("sys:dict:list")
    public List<SysDict> getSysDictTypeList() {
        return dicDao.list();
    }

}
