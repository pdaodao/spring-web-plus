package com.github.pdaodao.springwebplus.ai.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMRequest;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatStreamingBodyWrap;
import com.github.pdaodao.springwebplus.ai.pojo.HttpMsgSender;
import com.github.pdaodao.springwebplus.ai.service.AiChatDispatcher;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@Tag(name = "大模型问答")
@RestController
@AllArgsConstructor
@RequestMapping(Constant.ChatApiPrefix + "/chat")
public class AiChatCompletionsController {
    private final AiChatDispatcher chatDispatcher;

//    @PostMapping(path = "sse")
//    @Operation(summary = "流式问答")
//    public SseEmitter chatSse(@RequestBody LLMRequest chatReq) {
//        // 设置超时时间 10 分钟
//        final SseEmitter emitter = new SseEmitter(1000 * 60 * 10l);
//        chatDispatcher.sse(prepare(chatReq), emitter);
//        return emitter;
//    }

    @PostMapping(path = "streaming")
    @Operation(summary = "stream流式问答")
    public ResponseEntity<StreamingResponseBody> chatStream(@RequestBody LLMRequest chatReq) {
        final AiChatStreamingBodyWrap wrap = new AiChatStreamingBodyWrap(chatDispatcher, prepare(chatReq));
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache")
                .header("Content-Type", "text/plain")
                .header("Connection", "keep-alive")
                .header("X-Accel-Buffering", "no")
                .body(wrap);
    }

    @PostMapping(path = "http")
    @Operation(summary = "http问答")
    public LLMResponse chatHttp(@RequestBody LLMRequest chatReq) throws Exception{
        final HttpMsgSender msgSender = HttpMsgSender.of();
        chatDispatcher.http(prepare(chatReq), msgSender);
        return msgSender.getResponse();
    }

    private LLMRequest prepare(final LLMRequest req) {
        final LLMRequest rr = BeanUtil.copyProperties(req, LLMRequest.class);
        rr.setUserId(RequestUtil.getUserId());
        rr.setTeamId(RequestUtil.getTeamOrDefault());
        return rr;
    }
}