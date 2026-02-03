package com.github.pdaodao.springwebplus.ai.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LLMUsage {
    @JsonProperty("prompt_tokens")
    private Integer promptTokens;

    @JsonProperty("completion_tokens")
    private Integer completionTokens;

    @JsonProperty("total_tokens")
    private Integer totalTokens;

    public static LLMUsage of(final Integer promptTokens, final Integer completionTokens, final Integer totalTokens){
        final LLMUsage u = new LLMUsage();
        u.setPromptTokens(promptTokens);
        u.setCompletionTokens(completionTokens);
        u.setTotalTokens(totalTokens);
        return u;
    }
}
