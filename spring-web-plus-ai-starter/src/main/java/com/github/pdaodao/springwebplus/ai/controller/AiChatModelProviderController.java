package com.github.pdaodao.springwebplus.ai.controller;

import com.github.pdaodao.springwebplus.ai.base.ChatModelType;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelProviderDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModelProvider;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
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
@Tag(name = "模型供应商管理")
@RestController
@RequestMapping(Constant.ChatApiPrefix + "/model-provider")
@AllArgsConstructor
public class AiChatModelProviderController {
    private final AiChatModelProviderDao providerDao;
    private final AiChatModelDao modelDao;

    @GetMapping("list")
    @Operation(summary = "供应商列表")
    public List<AiChatModelProvider> providers(@RequestParam(required = false) final ChatModelType type) {
        return providerDao.list(type);
    }


    @PostMapping("save")
    @Operation(summary = "保存")
    public AiChatModelProvider save(@Validated @RequestBody AiChatModelProvider entity) {
        providerDao.save(entity);
        return entity;
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    public AiChatModelProvider info(@Parameter(name = "id", description = "主键") @RequestParam(required = false) final String id) {
        return providerDao.getById(id);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody IdWrap<String> idWrap) {
        final long cnt = modelDao.countByProvider(idWrap.getId());
        Preconditions.assertTrue(cnt > 0, "使用中无法删除");
        return providerDao.removeById(idWrap.getId());
    }
}