package com.github.pdaodao.springwebplus.ai.core;

import cn.hutool.core.collection.ListUtil;
import com.github.pdaodao.springwebplus.ai.base.AiChatModelOption;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.model.ChatModelBase;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.extensions.model.ollama.OllamaChatModel;
import io.agentscope.extensions.model.ollama.options.OllamaOptions;
import io.agentscope.extensions.model.ollama.options.ThinkOption;
import io.agentscope.extensions.model.openai.OpenAIChatModel;

public class Test {

    public static void main1(String[] args) {
        OpenAiEmbeddingModel api = new OpenAiEmbeddingModel("http://127.0.0.1:11434", "", "qwen3-embedding:0.6b", 1024);
        final float[]  ft = api.embed("北京欢迎您");
        System.out.println("a");
    }

    public static void main(String[] args) {
        final OpenAIChatModel chatModel = OpenAIChatModel.builder()
                .baseUrl("http://127.0.0.1:11434")
                .modelName("qwen3:8b")
                .build();

//        final ChatResponse ret = chatModel.chat(ListUtil.of(new UserMessage("你是谁")),
//                OllamaOptions.builder()
//                        .thinkOption(ThinkOption.ThinkBoolean.DISABLED)
//                .build());

        final ChatResponse ret = chatModel.stream(ListUtil.of(new UserMessage("你是谁")), null,
                GenerateOptions.builder()
                        //.thinkingBudget(100)
                        //.additionalBodyParam("think", false)
                        .stream(false).build()).blockLast();


        System.out.println("hello");
    }

    public static void main3(String[] args) {
        final ChatModelBase chat = AiChatModelUtil.ofOllama(AiChatModelOption.of("http://127.0.0.1:11434", null, "qwen3:8b"));
        final ChatResponse ret = chat.stream(ListUtil.of(new UserMessage("你是谁")), null,
                GenerateOptions.builder()
                        .thinkingBudget(100)
                        .stream(false).build()).blockLast();


        System.out.println("hello");
    }
}
