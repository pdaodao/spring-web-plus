package com.github.pdaodao.springwebplus.ai.pojo;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.base.MsgType;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import lombok.AllArgsConstructor;
import java.io.IOException;
import java.io.OutputStream;

@AllArgsConstructor
public class StreamingMsgSender implements MsgSender {
    private final LLMResponse response = new LLMResponse();
    private final OutputStream outputStream;

    @Override
    public LLMResponse getResponse() {
        return response;
    }

    private void sendText(final String text) throws IOException {
        if(StrUtil.isBlank(text)){
            return;
        }
        outputStream.write(StrUtil.utf8Bytes(text));
        outputStream.flush();
    }

    @Override
    public void sendMsg(final MsgBlock msgBlock) throws IOException {
        if(msgBlock == null){
            return;
        }
        sendText(JsonUtil.toJsonString(msgBlock));
        if(msgBlock.getUsage() != null){
            response.addBlock(msgBlock);
        }
    }

//    @Override
//    public void sendResponse(final LLMResponse response) throws IOException {
//        if(response == null){
//            return;
//        }
//        sendText(JsonUtil.toJsonString(response));
//    }

    @Override
    public void done(final AiChatContext context, final Exception e) throws IOException {
        outputStream.close();
    }

    @Override
    public void sendError(String errorMsg) throws IOException {
        if(StrUtil.isBlank(errorMsg)){
            return;
        }
        final MsgBlock msgBlock = new MsgBlock();
        msgBlock.setType(MsgType.error);
        msgBlock.setText(errorMsg);
        sendMsg(msgBlock);
    }
}
