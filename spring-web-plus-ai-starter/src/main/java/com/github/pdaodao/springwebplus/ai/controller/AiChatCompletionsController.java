package com.github.pdaodao.springwebplus.ai.controller;

import com.github.pdaodao.springwebplus.ai.pojo.AiChatReq;
import com.github.pdaodao.springwebplus.ai.util.Constant;
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
    @PostMapping(path = "completions", headers = "Accept=text/event-stream")
    @Operation(summary = "流式问答")
    public SseEmitter chatSse(@RequestBody AiChatReq chatReq) {
        // 设置超时时间，单位毫秒
        final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        // todo
        return emitter;
    }

    @PostMapping(path = "completions", headers = "!Accept=text/event-stream")
    @Operation(summary = "http问答")
    public void chatHttp(@RequestBody AiChatReq chatReq) {
        // todo
    }
}