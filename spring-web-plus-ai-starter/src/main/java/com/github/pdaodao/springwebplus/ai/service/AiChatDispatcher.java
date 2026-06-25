package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMRequest;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.base.MsgType;
import com.github.pdaodao.springwebplus.ai.dao.AiChatSessionDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatSessionMsgDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.entity.AiChatSession;
import com.github.pdaodao.springwebplus.ai.entity.AiChatSessionMsg;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.pojo.MsgSender;
import com.github.pdaodao.springwebplus.ai.util.AiChatDaoUtil;
import com.github.pdaodao.springwebplus.base.util.ExceptionUtil;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AiChatDispatcher {
    private final List<AiChatProcessor> processorList;
    private final AiChatSessionMsgDao sessionMsgDao;
    private final AiChatSessionDao sessionDao;

    private void lastPrepare(final AiChatContext context, final Exception e){
        context.getSessionMsg().setErrorMsg(e == null ? null : ExceptionUtil.getSimpleMsg(e));
        context.getSessionMsg().setAnswer(context.getResponse());
        if(context.getResponse() != null && context.getSessionMsg() != null){
            context.getResponse().setSessionId(context.getSessionMsg().getSessionId());
        }
        final long t2 = DateTimeUtil.currentTimeMillis();
        context.getResponse().setCost(t2 - context.getStartTime());
        context.getSessionMsg().setLlmCost((int)(t2 - context.getStartTime()));
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
        context.setPhase(req.getPhase());
        final AiChatApp app = AiChatDaoUtil.getAppById(req.getAppId());
        Preconditions.checkNotNull(app, "问答场景不存在.");
        context.setChatApp(app);
        context.setModelId(req.getModelId());
        if(StrUtil.isBlank(context.getModelId())){
            context.setModelId(app.getModelId());
        }
        context.setChatType(app.getChatType());
        if(StrUtil.isBlank(req.getDbId()) && app.getDbInfo() != null){
            req.setDbId(app.getDbInfo().getId());
        }
        if(StrUtil.isNotBlank(req.getDbId()) && CollUtil.isEmpty(req.getTableIds())){
            req.setTableIds(app.getTableList().stream().map(t -> t.getId()).collect(Collectors.toList()));
        }
        if(StrUtil.isBlank(req.getSessionId())){
            final AiChatSession session = new AiChatSession();
            session.setTeamId(req.getTeamId());
            session.setTitle(StrUtils.cut(req.getQuestion(), 64));
            session.setUserId(req.getUserId());
            session.setChatAppId(app.getId());
            sessionDao.save(session);
            req.setSessionId(session.getId());
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

    public void streaming(final LLMRequest req, final MsgSender msgSender) throws Exception {
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


    public void http(final LLMRequest req, final MsgSender msgSender) throws Exception{
        final AiChatContext context = prepare(req, msgSender);
        final long t1 = DateTimeUtil.currentTimeMillis();
        final AiChatProcessor p = selectProcessor(context);
        Preconditions.checkNotNull(p, "未找到处理逻辑:" + context.getChatType());
        try{
            p.http(context, msgSender);
            lastPrepare(context, null);
        }catch (Exception e){
            msgSender.sendError(ExceptionUtil.getSimpleMsg(e));
            lastPrepare(context, e);
        } finally {
            final long t2 = DateTimeUtil.currentTimeMillis();
            if(context.getResponse() != null){
                context.getResponse().setCost(t2 - t1);
            }
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