package com.github.pdaodao.aicompare.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class ZtChatService {
    private final ChatClient client;

    public ZtChatService(ChatModel chatModel) throws Exception {
        this.client = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(DashScopeChatOptions.builder()
                        .withTopP(0.7).build()
                ).build();
    }

    public ChatClient client(){
        return client;
    }
}