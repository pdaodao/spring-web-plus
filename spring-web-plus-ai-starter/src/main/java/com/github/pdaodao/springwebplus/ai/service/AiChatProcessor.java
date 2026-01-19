package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiChatProcessor {
    boolean accept(final AiChatContext context);

    void sse(final AiChatContext context, final SseEmitter sseEmitter);

    LLMResponse http(final AiChatContext context);
}