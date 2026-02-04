package com.github.pdaodao.springwebplus.ai.pojo;

import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;

import java.io.IOException;

public interface MsgSender {
    void sendMsg(final MsgBlock msgBlock) throws IOException;

    void sendResponse(final LLMResponse response) throws IOException;

    void done(final AiChatContext context, final Exception e) throws IOException;


    void sendError(final String errorMsg) throws IOException;
}
