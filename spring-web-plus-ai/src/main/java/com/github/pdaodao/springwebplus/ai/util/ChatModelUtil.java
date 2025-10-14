package com.github.pdaodao.springwebplus.ai.util;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMProvider;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatModelUtil {
    private static final Map<String, ChatModel> map = new ConcurrentHashMap<>();

    public static ChatModel of(LLMProvider provider, final String baseUrl, final String apiKey, final String modelName){
        Preconditions.checkNotBlank(baseUrl, "llm-model baseUrl is blank.");
        if(provider == null){
            provider = LLMProvider.openai;
        }
        final String key = provider+":"+baseUrl+":"+apiKey+":"+modelName;
        ChatModel model = map.get(key);
        if(model == null){
            synchronized (ChatModelUtil.class){
                model = map.get(key);
                if(model != null){
                    return model;
                }
                if(LLMProvider.openai == provider){
                    model = ofOpenAi(baseUrl, apiKey, modelName);
                    map.put(key, model);
                }
            }
        }
        return model;
    }

    private static ChatModel ofOpenAi(final String baseUrl, final String apiKey, final String modelName){
        final OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .completionsPath("/chat/completions")
                .embeddingsPath("/embeddings")
                .build();
        final OpenAiChatModel.Builder builder = OpenAiChatModel.builder();
        builder.openAiApi(openAiApi);
        if(StrUtil.isNotBlank(modelName)){
            builder.defaultOptions(OpenAiChatOptions.builder().model(modelName).build());
        }
        return builder.build();
    }
}
