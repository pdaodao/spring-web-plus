package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.AiChatType;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.LLMUsage;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.entity.AiChatText;
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
    private final AiChatTextService termTextService;
    @Override
    public boolean accept(AiChatContext context) {
        return AiChatType.TextGen == context.getChatType();
    }

    private List<Message> buildChatMessage(AiChatContext context, MsgSender sseEmitter) throws Exception{
        final List<Message> messages = new ArrayList<>();
        if(StrUtil.isNotBlank(context.getChatApp().getPrompt())){
            messages.add(SystemMessage.builder().text(context.getChatApp().getPrompt()).build());
        }
        // 业务术语
        if(context.getChatApp() != null && BooleanUtil.isTrue(context.getChatApp().getTermEnabled())){
            final List<AiChatText> textList = termTextService.search(context.getReq().getTeamId(), context.getReq().getQuestion());
            if(CollUtil.isNotEmpty(textList)){
                for(final AiChatText text: textList){
                    if(StrUtil.similar(text.getTitle(), context.getReq().getQuestion()) > 0.92){
                        final AiChatText info = termTextService.info(text.getId());
                        if(info == null){
                            continue;
                        }
                        final MsgBlock msgBlock = MsgBlock.ofText(info.getContent());
                        context.getResponse().addBlock(msgBlock);
                        context.getResponse().setUsage(LLMUsage.of(0, 0, 0));
                        if(sseEmitter != null){
                            sseEmitter.sendMsg(msgBlock);
                        }
                        return null;
                    }
                }
                final String text = AiChatTextService.buildMsg(textList);
                messages.add(SystemMessage.builder().text(text).build());
            }
        }
        // 用户问题
        messages.add(UserMessage.builder().text(context.getReq().getQuestion()).build());
        return messages;
    }

    @Override
    public void streaming(AiChatContext context, MsgSender sseEmitter) throws Exception {
        final ChatModel model = AiChatModelProvider.of(context.getReq().getTeamId(), context.getModelId());
        final List<Message> messages = buildChatMessage(context, sseEmitter);
        if(CollUtil.isEmpty(messages)){
            return;
        }
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
                try{
                    msgSender.sendMsg(msgBlock);
                }catch (Exception e){
                    log.error(e.getMessage(), e);
                }
            }finally {
                if(isEnd){
                    latch.countDown();
                }
            }
        }
    }

    @Override
    public void http(final AiChatContext context, final MsgSender sseEmitter) throws Exception{
        final ChatModel model = AiChatModelProvider.of(context.getReq().getTeamId(), context.getModelId());
        final List<Message> messages = buildChatMessage(context, sseEmitter);
        if(CollUtil.isEmpty(messages)){
            return;
        }
        final ChatResponse chatResponse = model.call(Prompt.builder().messages(messages).build());
        final Usage usage = chatResponse.getMetadata().getUsage();
        final String ret = chatResponse.getResult().getOutput().getText();
        sseEmitter.sendMsg(MsgBlock.ofText(ret));
        context.getResponse().setUsage(LLMUsage.of(usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens()));
    }
}
