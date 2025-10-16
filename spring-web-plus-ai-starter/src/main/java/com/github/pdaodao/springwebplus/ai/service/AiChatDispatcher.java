package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.RichLLMRequest;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.util.AiChatDaoUtil;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@AllArgsConstructor
public class AiChatDispatcher {
    private void prepare(final RichLLMRequest req) {
        if(StrUtil.isNotBlank(req.getAppId())){
            final AiChatApp app = AiChatDaoUtil.getAppById(req.getAppId());
            req.setChatType(app.getChatType());
            req.setChatModel(app.getChatModel());
        }
    }

    @Async
    public void sse(final RichLLMRequest req, final SseEmitter sseEmitter){
        prepare(req);
    }

    public LLMResponse http(final RichLLMRequest req){
        prepare(req);
        final LLMResponse rt = new LLMResponse();
        return rt;
    }
}
