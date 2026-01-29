package com.github.pdaodao.springwebplus.ai.core;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.base.AiChatModelOption;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AiChatModelUtil {
    private static final Map<String, ChatModel> chatModelMap = new ConcurrentHashMap<>();
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
    public static ChatModel of(final String provider, final AiChatModelOption option){
        if(StrUtil.containsIgnoreCase(provider, "ollama")){
            return ofOllama(option);
        }
        return ofOpenAi(option);
    }

    public static ChatModel ofOpenAi(final AiChatModelOption option){
        Preconditions.checkNotBlank(option.getModel(), "模型不能为空.");
        if(StrUtil.isBlank(option.getBaseUrl())){
            option.setBaseUrl("http://127.0.0.1:11434");
        }
        ChatModel chatModel = chatModelMap.get(option.getBaseUrl());
        if(chatModel != null){
            return chatModel;
        }
        synchronized (AiChatModelUtil.class){
            final String key = option.key();
            chatModel = chatModelMap.get(key);
            if(chatModel != null){
                return chatModel;
            }
            final OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(option.getBaseUrl())
                    .apiKey(option.getApiKey())
                    .build();
            final OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(OpenAiChatOptions.builder()
                            .model(option.getModel())
                            .temperature(option.getTemperature())
                            .build())
                    .build();
            chatModelMap.put(key, openAiChatModel);
            return openAiChatModel;
        }
    }

    public static ChatModel ofOllama(final AiChatModelOption option){
        Preconditions.checkNotBlank(option.getModel(), "模型不能为空.");
        if(StrUtil.isBlank(option.getBaseUrl())){
            option.setBaseUrl("http://127.0.0.1:11434");
        }
        ChatModel chatModel = chatModelMap.get(option.getBaseUrl());
        if(chatModel != null){
            return chatModel;
        }
        synchronized (AiChatModelUtil.class){
            final String key = option.key();
            chatModel = chatModelMap.get(key);
            if(chatModel != null){
                return chatModel;
            }
            final OllamaApi ollamaApi = OllamaApi.builder()
                    .baseUrl(option.getBaseUrl())
                    .build();
            final OllamaChatModel ollamaChatModel = OllamaChatModel.builder()
                    .ollamaApi(ollamaApi)
                    .defaultOptions(OllamaChatOptions.builder()
                            .model(option.getModel())
                            .temperature(option.getTemperature())
                            .numKeep(-1)
                            .disableThinking()
                            .build())
                    .build();
            chatModelMap.put(key, ollamaChatModel);
            return ollamaChatModel;
        }
    }

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        for(int i = 0; i < 10; i++){
            ChatModel chatModel = of("ollama", AiChatModelOption.of("http://127.0.0.1:11434", "123", "qwen3:8b"));
            final String ret = chatModel.call("你好,你是谁");
            System.out.println(ret);
        }
        long t2 = System.currentTimeMillis();
        System.out.println("cost:"+(t2 - t1));
    }
}
