package com.github.pdaodao.springwebplus.ai.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMRequest;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatStreamingBodyWrap;
import com.github.pdaodao.springwebplus.ai.service.AiChatDispatcher;
import com.github.pdaodao.springwebplus.base.auth.IgnoreLogin;
import com.github.pdaodao.springwebplus.entity.SysApiKey;
import com.github.pdaodao.springwebplus.util.SysApiKeyUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@Hidden
@RestController
@AllArgsConstructor
@RequestMapping("/chat/v1")
public class AiChatCompletionsOpenController {
    private final AiChatDispatcher chatDispatcher;

    @IgnoreLogin
    @PostMapping(path = "streaming")
    @Operation(summary = "stream流式问答")
    public ResponseEntity<StreamingResponseBody> chatStream(@RequestBody LLMRequest chatReq) {
        final LLMRequest req = prepare(chatReq);
        final AiChatStreamingBodyWrap wrap = new AiChatStreamingBodyWrap(chatDispatcher, req);
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache")
                .header("Content-Type", "text/plain")
                .header("Connection", "keep-alive")
                .header("X-Accel-Buffering", "no")
                .body(wrap);
    }

    @IgnoreLogin
    @PostMapping(path = "http")
    @Operation(summary = "http问答")
    public LLMResponse chatHttp(@RequestBody LLMRequest chatReq) {
        final LLMRequest req = prepare(chatReq);
        final LLMResponse rr = chatDispatcher.http(req);
        return rr;
    }

    private LLMRequest prepare(final LLMRequest req) {
        final LLMRequest rr = BeanUtil.copyProperties(req, LLMRequest.class);
        final SysApiKey apiKey = SysApiKeyUtil.check();
        rr.setTeamId(apiKey.getTeamId());
        return rr;
    }
}