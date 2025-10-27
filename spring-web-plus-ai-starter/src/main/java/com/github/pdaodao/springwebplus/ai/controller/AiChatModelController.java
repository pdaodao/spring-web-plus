package com.github.pdaodao.springwebplus.ai.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.ChatModelType;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelProviderDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModelProvider;
import com.github.pdaodao.springwebplus.ai.core.ChatModelUtil;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "模型管理")
@RestController
@RequestMapping(Constant.ChatApiPrefix + "/model")
@AllArgsConstructor
public class AiChatModelController {
    private final AiChatModelProviderDao providerDao;
    private final AiChatModelDao dao;

    @GetMapping("providers")
    @Operation(summary = "供应商列表")
    public List<AiChatModelProvider> providers(@RequestParam(required = false) final ChatModelType type) {
        return providerDao.list(type);
    }

    @GetMapping("list")
    @Operation(summary = "列表")
    public List<AiChatModel> list(@RequestParam(required = false) final ChatModelType type) {
        final List<AiChatModel> list = dao.list(type, RequestUtil.getTeamId());
        for (final AiChatModel t : list) {
            t.setApiKey(Constant.FakePassword);
        }
        return list;
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public AiChatModel save(@Validated @RequestBody AiChatModel entity) {
        if (StrUtil.isBlank(entity.getApiKey()) || StrUtil.equals(Constant.FakePassword, entity.getApiKey())) {
            entity.setApiKey(null);
        }
        dao.save(entity);
        return entity;
    }

    @PostMapping("test")
    @Operation(summary = "测试连接")
    public String test(@Validated @RequestBody AiChatModel entity) {
        if (StrUtil.isBlank(entity.getId()) && StrUtil.equals(Constant.FakePassword, entity.getApiKey())) {
            final AiChatModel old = dao.getById(entity.getId());
            if (old != null) {
                entity.setApiKey(old.getApiKey());
            }
        }
        final ChatModel chatModel = ChatModelUtil.of(entity.getProviderId(), entity.getBaseUrl(), entity.getApiKey(), entity.getModel());
        return chatModel.call("你好 你是谁");
    }


    @GetMapping("info")
    @Operation(summary = "详情")
    public AiChatModel info(@Parameter(name = "id", description = "主键") @RequestParam(required = false) final String id) {
        final AiChatModel t = BeanUtil.copyProperties(dao.detail(id), AiChatModel.class);
        if (t != null) {
            t.setApiKey(Constant.FakePassword);
        }
        return t;
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody IdWrap<String> idWrap) {
        return dao.removeById(idWrap.getId());
    }
}