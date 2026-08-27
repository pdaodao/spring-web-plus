package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.AiChatType;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.entity.AiChatText;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.pojo.MsgSender;
import com.github.pdaodao.springwebplus.ai.util.AiTextUtil;
import com.github.pdaodao.springwebplus.ai.util.ToTraditionalMsgProcessor;
import com.github.pdaodao.springwebplus.base.util.ExceptionUtil;
import io.agentscope.core.message.*;
import io.agentscope.core.model.ChatModelBase;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.ChatUsage;
import io.agentscope.core.model.GenerateOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AiChatLLMProcessor implements AiChatProcessor{
    private final AiChatTextService termTextService;
    @Override
    public boolean accept(AiChatContext context) {
        return AiChatType.TextGen == context.getChatType();
    }

    private List<Msg> buildChatMessage(final AiChatContext context, final MsgSender sseEmitter) throws Exception{
        final List<Msg> messages = new ArrayList<>();
        if(StrUtil.isNotBlank(context.getChatApp().getPrompt())){
            messages.add(new SystemMessage(context.getChatApp().getPrompt()));
        }
        if(StrUtil.similar(context.getReq().getQuestion(), AiTextUtil.toSimple(context.getReq().getQuestion())) < 0.9){
            sseEmitter.setMsgBlockProcessor(ToTraditionalMsgProcessor.of());
        }
        // 业务术语
        if(context.getChatApp() != null && CollUtil.isNotEmpty(context.getChatApp().getTopics())){
            final List<String> topicIds = context.getChatApp().getTopics().stream().map(t -> t.getTopicId()).collect(Collectors.toList());
            final List<AiChatText> textList = termTextService.search(context.getReq().getTeamId(), context.getReq().getQuestion(), topicIds);
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
                messages.add(new SystemMessage(text));
            }else if(StrUtil.isNotBlank(context.getChatApp().getAppConfig().getNoTextTips())){
                final MsgBlock msgBlock = MsgBlock.ofText(context.getChatApp().getAppConfig().getNoTextTips());
                msgBlock.usage(0, 0, 0);
                sseEmitter.sendMsg(msgBlock);
                sseEmitter.saveMsg(msgBlock);
                return null;
            }
        }
        // 用户问题
        messages.add(new UserMessage(context.getReq().getQuestion()));
        return messages;
    }

    @Override
    public void streaming(AiChatContext context, MsgSender sseEmitter) throws Exception {
        final ChatModelBase model = AiChatModelProvider.of(context.getReq().getTeamId(), context.getModelId());
        final List<Msg> messages = buildChatMessage(context, sseEmitter);
        if(CollUtil.isEmpty(messages)){
            return;
        }
        final CountDownLatch latch = new CountDownLatch(1);
        final Flux<ChatResponse> fluxResp = model.stream(messages, null, null);
        fluxResp.subscribe(new ChatResponseConsumer(latch, sseEmitter));
        latch.await(10, TimeUnit.MINUTES);
    }

    public static class ChatResponseConsumer implements Subscriber<ChatResponse> {
        final CountDownLatch latch;
        private final MsgSender msgSender;
        final StringBuilder sb = new StringBuilder();
        private ChatUsage chatUsage;

        public ChatResponseConsumer(CountDownLatch latch, MsgSender msgSender) {
            this.latch = latch;
            this.msgSender = msgSender;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ChatResponse chatResponse) {
            String text = StrUtil.EMPTY;
            if(CollUtil.isNotEmpty(chatResponse.getContent())){
                if(chatResponse.getContent().get(0) instanceof TextBlock t){
                    text = t.getText();
                }
            }
            // todo
            final MsgBlock msgBlock = MsgBlock.ofText(text);
            sb.append(text);
            if(chatResponse.getUsage() != null){
                chatUsage = chatResponse.getUsage();
            }
            try{
                msgSender.sendMsg(msgBlock);
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            try{
                msgSender.sendMsg(MsgBlock.ofText(ExceptionUtil.getSimpleMsg(throwable)));
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }

        @Override
        public void onComplete() {
            final MsgBlock all = MsgBlock.ofText(sb.toString());
            if(chatUsage != null){
                all.usage(chatUsage.getInputTokens(), chatUsage.getOutputTokens(), chatUsage.getTotalTokens());
            }
            msgSender.saveMsg(all);
            latch.countDown();
        }
    }

    @Override
    public void http(final AiChatContext context, final MsgSender sseEmitter) throws Exception{
        final ChatModelBase model = AiChatModelProvider.of(context.getReq().getTeamId(), context.getModelId());
        final List<Msg> messages = buildChatMessage(context, sseEmitter);
        if(CollUtil.isEmpty(messages)){
            return;
        }
        final ChatResponse chatResponse = model.stream(messages, null,
                GenerateOptions.builder().stream(false).build()).blockLast();
        final ChatUsage usage = chatResponse.getUsage();
        if(CollUtil.isNotEmpty(chatResponse.getContent())){
            for(final ContentBlock b: chatResponse.getContent()){
                if(b instanceof TextBlock t){
                    final String ret = t.getText();
                    final MsgBlock msgBlock = MsgBlock.ofText(ret);
                    msgBlock.usage(usage.getInputTokens(), usage.getOutputTokens(), usage.getTotalTokens());
                    sseEmitter.sendMsg(msgBlock);
                }
            }
        }
    }
}