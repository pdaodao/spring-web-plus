package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.base.LLMRequest;
import com.github.pdaodao.springwebplus.ai.base.RichLLMRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiChatProcessor {
    boolean accept(final RichLLMRequest req);

    void sse(final RichLLMRequest req, final SseEmitter sseEmitter);

    LLMRequest http(final RichLLMRequest req);
}