package com.github.pdaodao.springwebplus.ai.pojo;

import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.base.MsgBlockProcessor;

import java.io.IOException;

public interface MsgSender {
    void setMsgBlockProcessor(MsgBlockProcessor processor);

    LLMResponse getResponse();

    void sendMsg(final MsgBlock msgBlock) throws IOException;

    void saveMsg(final MsgBlock msgBlock);

    // void sendResponse(final LLMResponse response) throws IOException;

    void done(final AiChatContext context, final Exception e) throws IOException;


    void sendError(final String errorMsg) throws IOException;
}
