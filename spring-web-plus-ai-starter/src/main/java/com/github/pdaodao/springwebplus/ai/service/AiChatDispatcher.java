package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMRequest;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.base.MsgType;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatSessionMsgDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.ai.entity.AiChatSessionMsg;
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
    private final AiChatSessionMsgDao sessionMsgDao;

    private void lastPrepare(final AiChatContext context, final Exception e){
        context.getSessionMsg().setErrorMsg(e == null ? null : ExceptionUtil.getSimpleMsg(e));
        context.getSessionMsg().setAnswer(context.getResponse());
        if(context.getResponse().getUsage() != null){
            context.getSessionMsg().setTotalTokens(context.getResponse().getUsage().getTotalTokens());
            context.getSessionMsg().setInputTokens(context.getResponse().getUsage().getPromptTokens());
            context.getSessionMsg().setOutputTokens(context.getResponse().getUsage().getCompletionTokens());
        }
        sessionMsgDao.save(context.getSessionMsg());
    }

    private AiChatContext prepare(final LLMRequest req, final MsgSender msgSender) {
        Preconditions.checkNotBlank(req.getAppId(), "问答场景id不能为空.");
        final AiChatContext context = AiChatContext.of(req, msgSender);
        context.setResponse(new LLMResponse());
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
        if(StrUtil.isBlank(req.getDbId()) && app.getDbInfo() != null){
            req.setDbId(app.getDbInfo().getId());
        }
        if(StrUtil.isNotBlank(req.getDbId()) && CollUtil.isEmpty(req.getTableIds())){
            req.setTableIds(app.getTableList().stream().map(t -> t.getId()).collect(Collectors.toList()));
        }
        final AiChatSessionMsg sessionMsg = new AiChatSessionMsg();
        sessionMsg.setQuestion(req.getQuestion());
        sessionMsg.setSessionId(req.getSessionId());
        sessionMsg.setAppId(req.getAppId());
        sessionMsg.setDbId(req.getDbId());
        sessionMsgDao.save(sessionMsg);
        context.setSessionMsg(sessionMsg);
        return context;
    }

    public void streaming(final LLMRequest req, final MsgSender msgSender) throws IOException {
        final AiChatContext context = prepare(req, msgSender);
        try {
            final AiChatProcessor p = selectProcessor(context);
            if (p == null) {
                msgSender.sendError("未找到处理逻辑:" + context.getChatType());
            }
            p.streaming(context, msgSender);
            lastPrepare(context, null);
        } catch (final Exception e) {
            msgSender.sendError(ExceptionUtil.getSimpleMsg(e));
            lastPrepare(context, e);
        } finally {
            msgSender.sendMsg(MsgBlock.of(MsgType.done, null));
            AiChatContext.clear();
        }
    }


    public LLMResponse http(final LLMRequest req) {
        final AiChatContext context = prepare(req, null);
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