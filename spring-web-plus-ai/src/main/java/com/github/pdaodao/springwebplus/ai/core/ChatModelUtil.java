package com.github.pdaodao.springwebplus.ai.core;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMProvider;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatModelUtil {
    private static final Map<String, OpenAiApi> openAiApiMap = new ConcurrentHashMap<>();

    public static AiChatModelFactory ofFactory(final LLMProvider provider, final String baseUrl, final String apiKey,
                                               final ChatOptions chatOptions) {
        final OpenAiApi openAiApi = openAiApi(baseUrl, apiKey);
        return new AiChatModelFactory(openAiApi, chatOptions);
    }

    public static ChatModel of(LLMProvider provider, final String baseUrl, final String apiKey, final String modelName) {
        Preconditions.checkNotBlank(baseUrl, "llm-model baseUrl is blank.");
        if (provider == null) {
            provider = LLMProvider.openai;
        }
        if (LLMProvider.openai == provider) {
            return ofOpenAi(baseUrl, apiKey, modelName);
        }
        return ofOpenAi(baseUrl, apiKey, modelName);
    }

    private static ChatModel ofOpenAi(final String baseUrl, final String apiKey, final String modelName) {
        final OpenAiApi openAiApi = openAiApi(baseUrl, apiKey);
        final OpenAiChatModel.Builder builder = OpenAiChatModel.builder();
        builder.openAiApi(openAiApi);
        if (StrUtil.isNotBlank(modelName)) {
            builder.defaultOptions(OpenAiChatOptions.builder().model(modelName).build());
        }
        return builder.build();
    }

    public static OpenAiApi openAiApi(final String baseUrl, final String apiKey) {
        final String key = StrUtil.toStringOrEmpty(baseUrl) + ":" + StrUtil.toStringOrEmpty(apiKey);
        final OpenAiApi old = openAiApiMap.get(key);
        if (old != null) {
            return old;
        }
        synchronized (ChatModelUtil.class) {
            final OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(baseUrl)
                    .apiKey(apiKey)
                    .completionsPath("/chat/completions")
                    .embeddingsPath("/embeddings")
                    .build();
            openAiApiMap.put(key, openAiApi);
            return openAiApi;
        }
    }
}
