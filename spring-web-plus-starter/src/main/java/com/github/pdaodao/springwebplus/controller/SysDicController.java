package com.github.pdaodao.springwebplus.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.dao.SysDicDao;
import com.github.pdaodao.springwebplus.entity.SysDic;
import com.github.pdaodao.springwebplus.entity.SysDicValue;
import com.github.pdaodao.springwebplus.tool.data.ListWrap;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "系统字典")
@RequestMapping(Constant.ApiPrefix + "/dic")
@AllArgsConstructor
public class SysDicController {
    private final SysDicDao dicDao;

    @Operation(summary = "字典树")
    @GetMapping("tree")
    public List<SysDic> tree(){
        final List<SysDic> list = dicDao.infoList();
        return IdUtil.toTree(BeanUtils.copyToList(list, SysDic.class), SysDic::getId, SysDic::getPid);
    }

    @PostMapping("/save")
    @Operation(summary = "字典保存")
    public SysDic save(@Valid @RequestBody SysDic dic) {
        if(StrUtil.isBlank(dic.getPid())){
            dic.setPid("0");
        }
        dicDao.save(dic);
        return dic;
    }

    @PostMapping("/delete")
    @Operation(summary = "删除字典")
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDict(@Validated @RequestBody IdWrap<String> idWrap) {
        final List<SysDic> subs = dicDao.byPid(idWrap.getId());
        Preconditions.assertTrue(CollUtil.isNotEmpty(subs), "存在子项不允许删除");
        dicDao.removeById(idWrap.getId());
        return true;
    }

    @GetMapping("/values")
    @Operation(summary = "字典值列表")
    public List<SysDicValue> valueList(final String dicId, final PageRequestParam pageRequestParam) {
        PageHelper.startPage(pageRequestParam);
        return dicDao.valuePage(dicId, pageRequestParam.getQ());
    }

    @PostMapping("/values-delete")
    @Operation(summary = "删除字典值")
    public Boolean valueDelete(@RequestBody ListWrap<String> ids){
        if(ids.empty()){
            return false;
        }
        return dicDao.valueDelete(ids.getList());
    }

    @PostMapping("/value-save")
    @Operation(summary = "单个字典值修改")
    public SysDicValue valueSave(@RequestBody SysDicValue entity){
        return dicDao.valueSave(entity);
    }

    @PostMapping("/values-save")
    @Operation(summary = "批量字典值修改")
    public Boolean valuesSave(@RequestBody ListWrap<SysDicValue> list){
        if(list.empty()){
            return false;
        }
        final String dicId = list.getList().get(0).getDicId();
        Preconditions.checkNotBlank(dicId, "字典id不能为空.");
        return dicDao.valuesSave(list.getList());
    }
}