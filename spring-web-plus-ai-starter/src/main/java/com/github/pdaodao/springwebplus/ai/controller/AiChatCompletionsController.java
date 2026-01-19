package com.github.pdaodao.springwebplus.ai.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMRequest;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.service.AiChatDispatcher;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Tag(name = "大模型问答")
@RestController
@AllArgsConstructor
@RequestMapping(Constant.ChatApiPrefix + "/chat")
public class AiChatCompletionsController {
    private final AiChatDispatcher chatDispatcher;

    @PostMapping(path = "sse")
    @Operation(summary = "流式问答")
    public SseEmitter chatSse(@RequestBody LLMRequest chatReq) {
        // 设置超时时间 10 分钟
        final SseEmitter emitter = new SseEmitter(1000 * 60 * 10l);
        chatDispatcher.sse(prepare(chatReq), emitter);
        return emitter;
    }

    @PostMapping(path = "http")
    @Operation(summary = "http问答")
    public LLMResponse chatHttp(@RequestBody LLMRequest chatReq) {
        final LLMResponse rr = chatDispatcher.http(prepare(chatReq));
        return rr;
    }

    private LLMRequest prepare(final LLMRequest req) {
        final LLMRequest rr = BeanUtil.copyProperties(req, LLMRequest.class);
        rr.setUserId(RequestUtil.getUserId());
        rr.setTeamId(RequestUtil.getTeamOrDefault());
        return rr;
    }
}