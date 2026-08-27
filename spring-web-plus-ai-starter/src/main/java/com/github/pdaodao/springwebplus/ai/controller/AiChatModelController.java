package com.github.pdaodao.springwebplus.ai.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.base.ChatModelType;
import com.github.pdaodao.springwebplus.ai.core.AiChatModelUtil;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelProviderDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModelProvider;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.agentscope.core.message.ContentBlock;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.model.ChatModelBase;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.GenerateOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.StringBufferInputStream;
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
        final List<AiChatModel> list = dao.list(type, RequestUtil.getTeamId(), null);
        for (final AiChatModel t : list) {
            t.setApiKey(Constant.FakePassword);
        }
        return list;
    }

    @PostMapping("setEnabled")
    @Operation(summary = "设置是否启用")
    public Boolean setEnabled(@RequestBody AiChatModel entity) {
        Preconditions.checkNotBlank(entity.getId(), "id不能为空");
        Preconditions.checkNotNull(entity.getEnabled(), "是否启用不能为空");
        return dao.setEnabled(entity.getId(), entity.getEnabled());
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
        if (StrUtil.isNotBlank(entity.getId()) && StrUtil.equals(Constant.FakePassword, entity.getApiKey())) {
            final AiChatModel old = dao.getById(entity.getId());
            if (old != null) {
                entity.setApiKey(old.getApiKey());
            }
        }
        if(entity.getType() == null || entity.getType() == ChatModelType.LLM){
            final ChatModelBase chatModel = AiChatModelUtil.of(entity.getProviderId(), entity.toOption());
            final ChatResponse resp = chatModel.stream(ListUtil.of(new UserMessage("你好 你是谁")), null, GenerateOptions.builder()
                    .additionalBodyParam("think", false).stream(false).build())
                    .blockFirst();
            final StringBuilder sb = new StringBuilder();
            for(final ContentBlock b: resp.getContent()){
                if(b instanceof TextBlock t){
                    if(sb.length() > 1){
                        sb.append("\n");
                    }
                    sb.append(t.getText());
                }
            }
            return sb.toString();
        }
        if(ChatModelType.EMBEDDING == entity.getType()){
            final AiEmbedding aiEmbedding = AiChatModelUtil.ofEmbedding(null, entity.toOption());
            final float[] ft = aiEmbedding.embed("你好 你是谁");
            if(ArrayUtil.length(ft) == 1024){
                return "连接成功,向量纬度为1024";
            }
            return "不支持的纬度,系统要求的向量纬度为1024";
        }
        return "不支持的模型";
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