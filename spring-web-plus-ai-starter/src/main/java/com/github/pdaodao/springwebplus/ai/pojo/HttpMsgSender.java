package com.github.pdaodao.springwebplus.ai.pojo;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.base.MsgBlockProcessor;
import com.github.pdaodao.springwebplus.ai.base.MsgType;
import java.io.IOException;

public class HttpMsgSender implements MsgSender {
    private final LLMResponse response;
    private MsgBlockProcessor msgBlockProcessor;

    public HttpMsgSender() {
        this.response = new LLMResponse();
    }

    public static HttpMsgSender of(){
        return new HttpMsgSender();
    }

    public LLMResponse getResponse(){
        return response;
    }

    @Override
    public void setMsgBlockProcessor(MsgBlockProcessor processor) {
        msgBlockProcessor = processor;
    }

    @Override
    public void sendMsg(final MsgBlock msgBlock) throws IOException {
        if(msgBlock == null){
            return;
        }
        if(msgBlockProcessor != null){
            msgBlockProcessor.process(msgBlock);
        }
        response.addBlock(msgBlock);
    }

    @Override
    public void saveMsg(MsgBlock msgBlock) {
    }

    @Override
    public void done(final AiChatContext context, final Exception e) throws IOException {
    }

    @Override
    public void sendError(String errorMsg) throws IOException {
        if(StrUtil.isBlank(errorMsg)){
            return;
        }
        final MsgBlock msgBlock = new MsgBlock();
        msgBlock.setType(MsgType.error);
        msgBlock.setText(errorMsg);
        msgBlock.usage(0, 0, 0);
        sendMsg(msgBlock);
    }
}