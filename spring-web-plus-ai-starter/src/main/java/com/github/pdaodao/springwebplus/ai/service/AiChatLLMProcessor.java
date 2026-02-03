package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.AiChatType;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.LLMUsage;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.pojo.MsgSender;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class AiChatLLMProcessor implements AiChatProcessor{
    @Override
    public boolean accept(AiChatContext context) {
        return AiChatType.TextGen == context.getChatType();
    }

    @Override
    public void streaming(AiChatContext context, MsgSender sseEmitter) throws IOException, InterruptedException {
        final ChatModel model = AiChatModelProvider.of(context.getReq().getTeamId(), context.getModelId());
        final CountDownLatch latch = new CountDownLatch(1);
        final Flux<ChatResponse> fluxResp = model.stream(Prompt.builder().content(context.getReq().getQuestion()).build());
        fluxResp.subscribe(new ChatResponseConsumer(latch, context, sseEmitter));
        latch.await(10, TimeUnit.MINUTES);
    }

    @AllArgsConstructor
    public static class ChatResponseConsumer implements Consumer<ChatResponse> {
        final CountDownLatch latch;
        private final AiChatContext context;
        private final MsgSender msgSender;
        final StringBuilder sb = new StringBuilder();

        @Override
        public void accept(final ChatResponse chatResponse) {
            boolean isEnd = false;
            try{
                final AssistantMessage msg = chatResponse.getResult().getOutput();
                final MsgBlock msgBlock = MsgBlock.ofText(msg.getText());
                sb.append(msg.getText());
                final String finishReason = chatResponse.getResult().getMetadata().getFinishReason();
                if(StrUtil.containsIgnoreCase(finishReason, "done")
                        || StrUtil.containsIgnoreCase(finishReason, "stop")){
                    msgBlock.setIsEnd(true);
                    isEnd = true;
                    context.getResponse().addBlock(MsgBlock.ofText(sb.toString()));
                    final Usage usage = chatResponse.getMetadata().getUsage();
                    context.getResponse().setUsage(LLMUsage.of(usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens()));
                }else{
                    msgBlock.setIsEnd(false);
                }
                msgBlock.setId(chatResponse.getMetadata().getId());
                try{
                    msgSender.sendJson(msgBlock);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }finally {
                if(isEnd){
                    latch.countDown();
                }
            }
        }
    }

    @Override
    public LLMResponse http(AiChatContext context) {
        return null;
    }
}
