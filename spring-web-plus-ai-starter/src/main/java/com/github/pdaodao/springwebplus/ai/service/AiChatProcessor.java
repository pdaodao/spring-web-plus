package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.RichLLMRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiChatProcessor {
    boolean accept(final RichLLMRequest req);

    void sse(final RichLLMRequest req, final SseEmitter sseEmitter);

    LLMResponse http(final RichLLMRequest req);
}