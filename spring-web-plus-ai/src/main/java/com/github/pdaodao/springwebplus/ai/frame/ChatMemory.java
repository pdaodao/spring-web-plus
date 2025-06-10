package com.github.pdaodao.springwebplus.ai.frame;

import com.github.pdaodao.springwebplus.ai.support.ChatMessage;

import java.util.List;

public interface ChatMemory {
    // TODO: consider a non-blocking interface for streaming usages

    default void add(String conversationId, ChatMessage message) {
        this.add(conversationId, List.of(message));
    }

    void add(String conversationId, List<ChatMessage> messages);

    List<ChatMessage> get(String conversationId, int lastN);

    void clear(String conversationId);

}
