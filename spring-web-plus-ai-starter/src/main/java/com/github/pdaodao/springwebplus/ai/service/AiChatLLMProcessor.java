package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.base.AiChatType;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.pojo.MsgSender;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class AiChatLLMProcessor implements AiChatProcessor{
    @Override
    public boolean accept(AiChatContext context) {
        return AiChatType.TextGen == context.getChatType();
    }

    @Override
    public void streaming(AiChatContext context, MsgSender sseEmitter) throws IOException {
        final ChatModel model = AiChatModelProvider.of(context.getReq().getTeamId(), context.getModelId());
        model.stream(context.getReq().getQuestion())
                        .subscribe(t -> {
                            try{
                                final MsgBlock block = MsgBlock.ofText(t);
                                sseEmitter.sendJson(block);
                            }catch (Exception e){

                            }
                        });
    }

    @Override
    public LLMResponse http(AiChatContext context) {
        return null;
    }
}
