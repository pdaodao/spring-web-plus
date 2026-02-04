package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.AiChatType;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.LLMUsage;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.entity.AiChatTermText;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.pojo.MsgSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Service
@AllArgsConstructor
public class AiChatLLMProcessor implements AiChatProcessor{
    private final AiChatTermTextService termTextService;
    @Override
    public boolean accept(AiChatContext context) {
        return AiChatType.TextGen == context.getChatType();
    }

    @Override
    public void streaming(AiChatContext context, MsgSender sseEmitter) throws IOException, InterruptedException {
        final ChatModel model = AiChatModelProvider.of(context.getReq().getTeamId(), context.getModelId());
        final StringBuilder sb = new StringBuilder();
        try{
            final List<AiChatTermText> list = termTextService.search(context.getReq().getTeamId(), context.getReq().getQuestion());
            if(CollUtil.isNotEmpty(list)){
                sb.append("已知如下信息:");
            }
            for(final AiChatTermText t: list){
                sb.append("\n").append(t.getRemark());
            }
            System.out.println("termTextService.search:"+sb.toString());
        }catch (final Exception e){
            log.error(e.getMessage(), e);
        }
        final List<Message> messages = new ArrayList<>();
        if(!sb.isEmpty()){
            messages.add(SystemMessage.builder().text(sb.toString()).build());
        }
        messages.add(UserMessage.builder().text(context.getReq().getQuestion()).build());
        final CountDownLatch latch = new CountDownLatch(1);
        final Flux<ChatResponse> fluxResp = model.stream(Prompt.builder().messages(messages).build());
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
                    msgSender.sendMsg(msgBlock);
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
