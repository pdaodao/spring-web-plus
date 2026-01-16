package com.github.pdaodao.springwebplus.ai.core;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.DefaultChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

@Data
@AllArgsConstructor
public class AiChatModelFactory {
    private final OpenAiApi openAiApi;
    private final ChatOptions chatOptions;

    public String getModelName(){
        return chatOptions.getModel();
    }

    public static AiChatModelFactory instance(final String provider,
                                              final String baseUrl, final String apiKey,
                                              ChatOptions chatOptions) {
        if (chatOptions == null) {
            chatOptions = new DefaultChatOptions();
        }
        return ChatModelUtil.ofFactory(provider, baseUrl, apiKey, chatOptions);
    }

    public static AiChatModelFactory instance(final String provider,
                                              final String baseUrl, final String apiKey,
                                              final String modelName) {
        final DefaultChatOptions chatOptions = new DefaultChatOptions();
        chatOptions.setModel(modelName);
        return ChatModelUtil.ofFactory(provider, baseUrl, apiKey, chatOptions);
    }

    public ChatModel of() {
        return of(new DefaultChatOptions());
    }

    public ChatModel of(ChatOptions options) {
        final OpenAiChatModel.Builder builder = OpenAiChatModel.builder();
        builder.openAiApi(openAiApi);
        if (options == null) {
            options = new DefaultChatOptions();
        }
        final String modelName = StrUtil.isNotBlank(options.getModel()) ? options.getModel() : chatOptions.getModel();
        final Double temperature = options.getTemperature() != null ? options.getTemperature() : chatOptions.getTemperature();
        if (StrUtil.isNotBlank(modelName)) {
            final OpenAiChatOptions.Builder b = OpenAiChatOptions.builder();
            b.model(modelName);
            if (temperature != null) {
                b.temperature(temperature);
            }
            builder.defaultOptions(b.build());
        }
        return builder.build();
    }
}
