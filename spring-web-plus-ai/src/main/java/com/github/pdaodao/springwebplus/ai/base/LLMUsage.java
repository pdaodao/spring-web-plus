package com.github.pdaodao.springwebplus.ai.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class LLMUsage {
    // inputTokens
    @JsonProperty("prompt_tokens")
    private Integer promptTokens;

    // outputTokens
    @JsonProperty("completion_tokens")
    private Integer completionTokens;


    @JsonProperty("total_tokens")
    private Integer totalTokens;

    // 耗时 秒
    private Float time;

    public static LLMUsage of(final Integer promptTokens, final Integer completionTokens, final Integer totalTokens){
        final LLMUsage u = new LLMUsage();
        u.setPromptTokens(promptTokens);
        u.setCompletionTokens(completionTokens);
        u.setTotalTokens(totalTokens);
        return u;
    }

//    public static LLMUsage of(final Usage usage){
//        if(usage == null){
//            return null;
//        }
//        final LLMUsage u = new LLMUsage();
//        u.setPromptTokens(usage.getPromptTokens());
//        u.setCompletionTokens(usage.getCompletionTokens());
//        u.setTotalTokens(usage.getTotalTokens());
//        return u;
//    }
}
