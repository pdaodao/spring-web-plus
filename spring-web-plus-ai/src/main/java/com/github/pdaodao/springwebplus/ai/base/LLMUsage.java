package com.github.pdaodao.springwebplus.ai.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.ai.chat.metadata.Usage;

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

    public static LLMUsage of(final Usage usage){
        if(usage == null){
            return null;
        }
        final LLMUsage u = new LLMUsage();
        u.setPromptTokens(usage.getPromptTokens());
        u.setCompletionTokens(usage.getCompletionTokens());
        u.setTotalTokens(usage.getTotalTokens());
        return u;
    }
}
