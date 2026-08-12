package com.github.pdaodao.springwebplus.ai.core;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.base.AiChatModelOption;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.model.ChatModelBase;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.extensions.model.ollama.OllamaChatModel;
import io.agentscope.extensions.model.ollama.options.OllamaOptions;
import io.agentscope.extensions.model.ollama.options.ThinkOption;
import io.agentscope.extensions.model.openai.OpenAIChatModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AiChatModelUtil {
    private static final Map<String, ChatModelBase> chatModelMap = new ConcurrentHashMap<>();
    private static final Map<String, AiEmbedding>  embeddingMap = new ConcurrentHashMap<>();

    /**
     * 向量化模型
     * @param provider
     * @param option
     * @return
     */
    public static AiEmbedding ofEmbedding(final String provider, final AiChatModelOption option){
        final String key = option.key();
        AiEmbedding old = embeddingMap.get(key);
        if(old != null){
            return old;
        }
        synchronized (embeddingMap){
            old = embeddingMap.get(key);
            if(old != null){
                return old;
            }
            old = new OpenAiEmbedding(option.getBaseUrl(), option.getApiKey(), option.getModel(), 1024, 16);
            embeddingMap.put(key, old);
        }
        return old;
    }

    /**
     * 创建聊天模型
     * @param provider
     * @param option
     * @return
     */
    public static ChatModelBase of(final String provider, final AiChatModelOption option){
        if(StrUtil.containsIgnoreCase(provider, "ollama")){
            return ofOllama(option);
        }
        return ofOpenAi(option);
    }

    public static ChatModelBase ofOpenAi(final AiChatModelOption option){
        Preconditions.checkNotBlank(option.getModel(), "模型不能为空.");
        if(StrUtil.isBlank(option.getBaseUrl())){
            option.setBaseUrl("http://127.0.0.1:11434");
        }
        ChatModelBase chatModel = chatModelMap.get(option.getBaseUrl());
        if(chatModel != null){
            return chatModel;
        }
        synchronized (AiChatModelUtil.class){
            final String key = option.key();
            chatModel = chatModelMap.get(key);
            if(chatModel != null){
                return chatModel;
            }
            chatModel = OpenAIChatModel.builder()
                    .baseUrl(option.getBaseUrl())
                    .apiKey(option.getApiKey())
                    .modelName(option.getModel())
                    .build();
            chatModelMap.put(key, chatModel);
            return chatModel;
        }
    }

    public static ChatModelBase ofOllama(final AiChatModelOption option){
        Preconditions.checkNotBlank(option.getModel(), "模型不能为空.");
        if(StrUtil.isBlank(option.getBaseUrl())){
            option.setBaseUrl("http://127.0.0.1:11434");
        }
        ChatModelBase chatModel = chatModelMap.get(option.getBaseUrl());
        if(chatModel != null){
            return chatModel;
        }
        synchronized (AiChatModelUtil.class){
            final String key = option.key();
            chatModel = chatModelMap.get(key);
            if(chatModel != null){
                return chatModel;
            }
            chatModel = OllamaChatModel.builder()
                    .baseUrl(option.getBaseUrl())
                    .modelName(option.getModel())
                    .defaultOptions(OllamaOptions.builder()
                            .keepAlive("-1s")
                            .thinkOption(ThinkOption.ThinkLevel.ThinkBoolean.DISABLED)
                            .build())
                    .build();
            chatModelMap.put(key, chatModel);
            return chatModel;
        }
    }

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        for(int i = 0; i < 10; i++){
            ChatModelBase chatModel = of("ollama", AiChatModelOption.of("http://127.0.0.1:11434", "123", "qwen3:8b"));
            final String ret = chatModel.stream(ListUtil.of(UserMessage.builder().textContent("你好,你是谁").build()),
                    null,
                    GenerateOptions.builder().stream(false).build()).blockFirst().toString();
            System.out.println(ret);
        }
        long t2 = System.currentTimeMillis();
        System.out.println("cost:"+(t2 - t1));
    }
}