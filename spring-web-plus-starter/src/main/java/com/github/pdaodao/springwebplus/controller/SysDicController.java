package com.github.pdaodao.springwebplus.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.dao.SysDicDao;
import com.github.pdaodao.springwebplus.entity.SysDic;
import com.github.pdaodao.springwebplus.tool.data.ListWrap;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "系统字典")
@RequestMapping(Constant.ApiPrefix + "/dic")
@AllArgsConstructor
public class SysDicController {
    private final SysDicDao dicDao;

    @GetMapping("list")
    public List<SysDic> list(@RequestParam(required = false) String pid,
                                      @RequestParam(required = false) final String title,
                                      final PageRequestParam pageRequestParam){
        if(StrUtil.isBlank(pid)){
            pid = "0";
        }
        PageHelper.startPage(pageRequestParam);
        return dicDao.list(pid, title);
    }

    @PostMapping("/save")
    @Operation(summary = "保存")
    public SysDic save(@Valid @RequestBody SysDic dic) {
        if(StrUtil.isBlank(dic.getPid())){
            dic.setPid("0");
        }
        dicDao.save(dic);
        if(CollUtil.isNotEmpty(dic.getChildren())){
            dicDao.saveValues(dic.getId(), dic.getChildren());
        }
        if(!StrUtil.equals("0", dic.getPid())){
            dicDao.clearValue(dic.getPid());
        }
        dicDao.allListClear();
        return dic;
    }

    @PostMapping("/delete")
    @Operation(summary = "删除字典")
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSysDictType(@RequestBody ListWrap<String> ids) {
        for(final String id: ids.getList()){
            final SysDic entity = dicDao.getById(id);
            dicDao.removeById(id);
            if(!StrUtil.equals("0", entity.getPid())){
                dicDao.clearValue(entity.getPid());
            }
        }
        dicDao.allListClear();
        return true;
    }

    @GetMapping("/info")
    @Operation(summary = "字典详情")
    public SysDic info(final String id) {
        final SysDic entity = dicDao.getById(id);
        entity.setChildren(dicDao.values(id));
        return entity;
    }

    @GetMapping("mapValues")
    @Operation(summary = "全部字典项值map")
    public Map<String, List<SysDic>> mapValues(){
        final List<SysDic> list = dicDao.allList();
        final List<SysDic> ret = BeanUtils.copyToList(list, SysDic.class);
        final Map<String, List<SysDic>> map = new LinkedHashMap<>();
        final List<SysDic> tree = IdUtil.toTree(ret, SysDic::getId, SysDic::getPid);
        for(final SysDic d: tree){
            map.put(d.getName(), d.getChildren());
        }
        return map;
    }
}
