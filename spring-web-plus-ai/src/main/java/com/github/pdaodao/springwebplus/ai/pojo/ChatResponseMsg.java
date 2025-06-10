package com.github.pdaodao.springwebplus.ai.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.ai.support.Usage;
import lombok.Data;
import java.util.List;

@Data
public class ChatResponseMsg {
    private String sessionId;
    private String msgId;

    private ChatContentType contentType;

    // finished
    private String msgStatus;

    private Boolean incremental;

    private List<ChatMsgContent> contents;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Usage usage;
}
