package com.github.pdaodao.springwebplus.ai.util;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

@Data
@AllArgsConstructor
public class AiChatModelFactory {
    private final OpenAiApi openAiApi;
    private final String modelName;

    public static AiChatModelFactory of(LLMProvider provider, final String baseUrl, final String apiKey, final String modelName){
       return ChatModelUtil.ofFactory(provider, baseUrl, apiKey, modelName);
    }

    public ChatModel of(){
        final OpenAiChatModel.Builder builder = OpenAiChatModel.builder();
        builder.openAiApi(openAiApi);
        if (StrUtil.isNotBlank(modelName)) {
            builder.defaultOptions(OpenAiChatOptions.builder().model(modelName).build());
        }
        return builder.build();
    }

    public ChatModel of( OpenAiChatOptions options){
        final OpenAiChatModel.Builder builder = OpenAiChatModel.builder();
        builder.openAiApi(openAiApi);
        if(options == null){
            options = OpenAiChatOptions.builder().build();
        }
        if(StrUtil.isBlank(options.getModel()) && StrUtil.isBlank(modelName)){
            options.setModel(modelName);
        }
        return builder.build();
    }
}
