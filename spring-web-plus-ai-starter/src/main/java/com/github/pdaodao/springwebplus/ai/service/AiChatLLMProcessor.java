package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.AiChatType;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.entity.AiChatText;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.pojo.MsgSender;
import com.github.pdaodao.springwebplus.ai.util.AiTextUtil;
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
        if(context.getChatApp() != null && CollUtil.isNotEmpty(context.getChatApp().getTopics())){
            final List<AiChatText> textList = termTextService.search(context.getReq().getTeamId(), context.getReq().getQuestion());
            if(CollUtil.isNotEmpty(textList)){
                for(final AiChatText text: textList){
                    if(AiTextUtil.similar(text.getTitle(), context.getReq().getQuestion()) > 0.92){
                        final AiChatText info = termTextService.info(text.getId());
                        if(info == null){
                            continue;
                        }
                        final MsgBlock msgBlock = MsgBlock.ofText(info.getContent());
                        msgBlock.usage(0, 0, 0);
                        sseEmitter.sendMsg(msgBlock);
                        sseEmitter.saveMsg(msgBlock);
                        return null;
                    }
                }
                final String text = AiChatTextService.buildMsg(textList);
                log.info(text);
                messages.add(SystemMessage.builder().text(text).build());
            }else if(StrUtil.isNotBlank(context.getChatApp().getAppConfig().getNoTextTips())){
                final MsgBlock msgBlock = MsgBlock.ofText(context.getChatApp().getAppConfig().getNoTextTips());
                msgBlock.usage(0, 0, 0);
                sseEmitter.sendMsg(msgBlock);
                sseEmitter.saveMsg(msgBlock);
                return null;
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
        fluxResp.subscribe(new ChatResponseConsumer(latch, sseEmitter));
        latch.await(10, TimeUnit.MINUTES);
    }

    @AllArgsConstructor
    public static class ChatResponseConsumer implements Consumer<ChatResponse> {
        final CountDownLatch latch;
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
                    isEnd = true;
                    final Usage usage = chatResponse.getMetadata().getUsage();
                    final MsgBlock all = MsgBlock.ofText(sb.toString());
                    all.usage(usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
                    msgSender.saveMsg(all);
                }
                try{
                    msgSender.sendMsg(msgBlock);
                }catch (Exception e){
                    log.error(e.getMessage(), e);
                }
            }catch (Exception e){
                latch.countDown();
            }finally{
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
        final MsgBlock msgBlock = MsgBlock.ofText(ret);
        msgBlock.usage(usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        sseEmitter.sendMsg(msgBlock);
    }
}