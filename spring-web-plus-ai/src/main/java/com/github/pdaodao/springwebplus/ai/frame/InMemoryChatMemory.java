package com.github.pdaodao.springwebplus.ai.frame;

import com.github.pdaodao.springwebplus.ai.support.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryChatMemory implements ChatMemory {

    Map<String, List<ChatMessage>> conversationHistory = new ConcurrentHashMap<>();

    @Override
    public void add(String conversationId, List<ChatMessage> messages) {
        this.conversationHistory.putIfAbsent(conversationId, new ArrayList<>());
        this.conversationHistory.get(conversationId).addAll(messages);
    }

    @Override
    public List<ChatMessage> get(String conversationId, int lastN) {
        List<ChatMessage> all = this.conversationHistory.get(conversationId);
        return all != null ? all.stream().skip(Math.max(0, all.size() - lastN)).toList() : List.of();
    }

    @Override
    public void clear(String conversationId) {
        this.conversationHistory.remove(conversationId);
    }

}