package com.github.pdaodao.springwebplus.ai.pojo;

import com.github.pdaodao.springwebplus.ai.base.LLMRequest;
import com.github.pdaodao.springwebplus.ai.service.AiChatDispatcher;
import lombok.AllArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.IOException;
import java.io.OutputStream;

@AllArgsConstructor
public class AiChatStreamingBodyWrap implements StreamingResponseBody {
    private final AiChatDispatcher chatDispatcher;
    private final LLMRequest request;

    @Override
    public void writeTo(final OutputStream outputStream) throws IOException {
        final StreamingMsgSender msgSender = new StreamingMsgSender(outputStream);
        try{
            chatDispatcher.streaming(request, msgSender);
        }catch (Exception e){
            throw new IOException(e);
        }
    }
}
