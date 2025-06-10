package com.github.pdaodao.springwebplus.ai.client;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.support.ChatResponseMsgListener;
import com.github.pdaodao.springwebplus.ai.support.LLMChatResponse;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

@Slf4j
@AllArgsConstructor
public class OpenAiListener extends EventSourceListener {
    private final ChatResponseMsgListener msgListener;

    @Override
    public void onEvent(EventSource source, String id, String type, String data) {
        if(StrUtil.equals("[DONE]", data)){
            msgListener.onClosed(id);
            return;
        }
        try{
            final LLMChatResponse response = JsonUtil.objectMapper.readValue(data, LLMChatResponse.class);
            System.out.println("Received SSE: " + data);
            System.out.println("json:"+JsonUtil.toJsonString(response));
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void onFailure(EventSource source, Throwable t, Response response) {
        System.err.println("SSE Error: " + t.getMessage());
    }
}
