package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.pojo.MsgSender;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public interface AiChatProcessor {
    boolean accept(final AiChatContext context);

    void streaming(final AiChatContext context, final MsgSender sseEmitter) throws IOException;

    LLMResponse http(final AiChatContext context);
}