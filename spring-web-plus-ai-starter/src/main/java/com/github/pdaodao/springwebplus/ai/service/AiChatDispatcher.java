package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.RichLLMRequest;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.util.AiChatDaoUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
@AllArgsConstructor
public class AiChatDispatcher {
    private final List<AiChatProcessor> processorList;

    private void prepare(final RichLLMRequest req) {
        if (StrUtil.isNotBlank(req.getAppId())) {
            final AiChatApp app = AiChatDaoUtil.getAppById(req.getAppId());
            req.setChatType(app.getChatType());
            req.setChatModel(app.getChatModel());
        }
    }

    @Async
    public void sse(final RichLLMRequest req, final SseEmitter sseEmitter) {
        prepare(req);
        try {
            final AiChatProcessor p = selectProcessor(req);
            if (p == null) {
                sseEmitter.send("未找到处理逻辑:" + req.getChatType());
            }
        } catch (final Exception e) {
            sseEmitter.completeWithError(e);
        } finally {
            sseEmitter.complete();
        }
    }

    public LLMResponse http(final RichLLMRequest req) {
        prepare(req);
        final AiChatProcessor p = selectProcessor(req);
        Preconditions.checkNotNull(p, "未找到处理逻辑:" + req.getChatType());
        final LLMResponse rr = p.http(req);
        return rr;
    }

    private AiChatProcessor selectProcessor(final RichLLMRequest req) {
        if (CollUtil.isEmpty(processorList)) {
            return null;
        }
        for (final AiChatProcessor p : processorList) {
            if (p.accept(req)) {
                return p;
            }
        }
        return null;
    }
}
