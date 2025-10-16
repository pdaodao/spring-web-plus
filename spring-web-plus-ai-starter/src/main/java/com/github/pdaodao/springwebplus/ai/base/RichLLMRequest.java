package com.github.pdaodao.springwebplus.ai.base;

import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import lombok.Data;

@Data
public class RichLLMRequest extends LLMRequest {
    private AiChatModel chatModel;

    private String userId;

    private String teamId;
}