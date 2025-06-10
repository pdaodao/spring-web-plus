package com.github.pdaodao.springwebplus.ai;


import com.github.pdaodao.springwebplus.ai.support.ChatResponseMsgListener;
import com.github.pdaodao.springwebplus.ai.support.LLMChatRequest;
import com.github.pdaodao.springwebplus.ai.support.LLMChatResponse;

public interface ChatClient {
    LLMChatResponse chat(final LLMChatRequest request);

    void chatStream(final LLMChatRequest request, ChatResponseMsgListener listener);
}