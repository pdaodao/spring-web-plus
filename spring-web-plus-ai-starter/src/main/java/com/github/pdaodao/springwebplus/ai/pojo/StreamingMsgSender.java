package com.github.pdaodao.springwebplus.ai.pojo;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import lombok.AllArgsConstructor;
import java.io.IOException;
import java.io.OutputStream;

@AllArgsConstructor
public class StreamingMsgSender implements MsgSender {
    private final OutputStream outputStream;

    @Override
    public void sendText(String text) throws IOException {
        if(StrUtil.isBlank(text)){
            return;
        }
        outputStream.write(StrUtil.utf8Bytes(text));
        outputStream.flush();
    }

    @Override
    public void done(final AiChatContext context, final Exception e) throws IOException {
        outputStream.close();
    }

    @Override
    public void sendJson(Object obj) throws IOException{
        if(obj == null){
            return;
        }
        sendText(JsonUtil.toJsonString(obj));
    }

    @Override
    public void sendError(String errorMsg) throws IOException {
        if(StrUtil.isBlank(errorMsg)){
            return;
        }
        sendText("Error:"+errorMsg);
    }
}
