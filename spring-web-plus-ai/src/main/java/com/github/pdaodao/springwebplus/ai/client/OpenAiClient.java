package com.github.pdaodao.springwebplus.ai.client;

import com.github.pdaodao.springwebplus.ai.ChatClient;
import com.github.pdaodao.springwebplus.ai.support.ChatResponseMsgListener;
import com.github.pdaodao.springwebplus.ai.support.LLMChatRequest;
import com.github.pdaodao.springwebplus.ai.support.LLMChatResponse;

public class OpenAiClient extends BaseClient implements ChatClient {

    public OpenAiClient(String sseUrl, String key) {
        super(sseUrl, key);
    }

    @Override
    public LLMChatResponse chat(LLMChatRequest request) {
        return null;
    }

    @Override
    public void chatStream(LLMChatRequest request, ChatResponseMsgListener listener) {
        
    }
}
