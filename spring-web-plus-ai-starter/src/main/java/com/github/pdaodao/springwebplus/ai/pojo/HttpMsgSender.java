package com.github.pdaodao.springwebplus.ai.pojo;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.base.MsgType;
import java.io.IOException;

public class HttpMsgSender implements MsgSender {
    private final LLMResponse response;

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
    public void sendMsg(final MsgBlock msgBlock) throws IOException {
        if(msgBlock == null || msgBlock.getUsage() == null){
            return;
        }
        response.addBlock(msgBlock);
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