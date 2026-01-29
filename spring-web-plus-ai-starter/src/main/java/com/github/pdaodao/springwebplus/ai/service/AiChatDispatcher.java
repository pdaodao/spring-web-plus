package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMRequest;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.pojo.MsgSender;
import com.github.pdaodao.springwebplus.ai.util.AiChatDaoUtil;
import com.github.pdaodao.springwebplus.base.util.ExceptionUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AiChatDispatcher {
    private final List<AiChatProcessor> processorList;

    private AiChatContext prepare(final LLMRequest req) {
        Preconditions.checkNotBlank(req.getAppId(), "问答场景id不能为空.");
        final AiChatContext context = AiChatContext.of(req);
        context.setPhase(req.getPhase());
        final AiChatApp app = AiChatDaoUtil.getAppById(req.getAppId());
        Preconditions.checkNotNull(app, "问答场景不存在.");
        context.setChatApp(app);

        context.setModelId(req.getModelId());
        if(StrUtil.isBlank(context.getModelId())){
            context.setModelId(app.getModelId());
        }
        context.setChatType(app.getChatType());
        if(StrUtil.isBlank(context.getModelId())){
            // 随机选择一个启用的模型
            final List<AiChatModel> list = SpringUtil.getBean(AiChatModelDao.class).list(null, req.getTeamId(), true);
            Preconditions.checkArgument(CollUtil.isNotEmpty(list), "请先添加模型");
            int random = RandomUtil.randomInt(CollUtil.size(list));
            context.setModelId(list.get(random).getId());
        }
        if(StrUtil.isBlank(req.getDbId())){
            req.setDbId(app.getDbInfo().getId());
        }
        if(StrUtil.isNotBlank(req.getDbId()) && CollUtil.isEmpty(req.getTableIds())){
            req.setTableIds(app.getTableList().stream().map(t -> t.getId()).collect(Collectors.toList()));
        }
        return context;
    }

    public void streaming(final LLMRequest req, final MsgSender msgSender) throws IOException {
        final AiChatContext context = prepare(req);
        try {
            final AiChatProcessor p = selectProcessor(context);
            if (p == null) {
                msgSender.sendError("未找到处理逻辑:" + context.getChatType());
            }
            p.streaming(context, msgSender);
        } catch (final Exception e) {
            msgSender.sendError(ExceptionUtil.getSimpleMsg(e));
        } finally {
            AiChatContext.clear();
        }
    }


    public LLMResponse http(final LLMRequest req) {
        final AiChatContext context = prepare(req);
        final AiChatProcessor p = selectProcessor(context);
        Preconditions.checkNotNull(p, "未找到处理逻辑:" + context.getChatType());
        try{
            final LLMResponse rr = p.http(context);
            return rr;
        }finally {
            AiChatContext.clear();
        }
    }

    private AiChatProcessor selectProcessor(final AiChatContext context) {
        if (CollUtil.isEmpty(processorList)) {
            return null;
        }
        for (final AiChatProcessor p : processorList) {
            if (p.accept(context)) {
                return p;
            }
        }
        return null;
    }
}