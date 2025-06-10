package com.github.pdaodao.springwebplus.ai.pojo;

import com.github.pdaodao.springwebplus.ai.support.ChatRole;
import lombok.Data;

@Data
public class ChatMsgContent {
    private String id;

    private String content;

    private String contentType;

    private ChatRole role;
}
