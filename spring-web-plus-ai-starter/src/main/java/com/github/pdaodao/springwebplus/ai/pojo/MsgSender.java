package com.github.pdaodao.springwebplus.ai.pojo;

import java.io.IOException;

public interface MsgSender {
    void sendText(String text) throws IOException;

    void done(final AiChatContext context, final Exception e) throws IOException;

    void sendJson(final Object obj) throws IOException;

    void sendError(final String errorMsg) throws IOException;
}
