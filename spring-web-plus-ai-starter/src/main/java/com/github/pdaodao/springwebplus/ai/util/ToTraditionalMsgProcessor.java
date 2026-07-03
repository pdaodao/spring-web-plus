package com.github.pdaodao.springwebplus.ai.util;

import com.github.pdaodao.springwebplus.ai.base.MsgBlock;
import com.github.pdaodao.springwebplus.ai.base.MsgBlockProcessor;

/**
 * 文本转为繁体中文
 */
public class ToTraditionalMsgProcessor implements MsgBlockProcessor {
    @Override
    public void process(final MsgBlock msgBlock) {
        if(msgBlock == null){
            return;
        }
        msgBlock.setText(AiTextUtil.toTraditional(msgBlock.getText()));
    }

    public static ToTraditionalMsgProcessor of(){
        return new ToTraditionalMsgProcessor();
    }
}