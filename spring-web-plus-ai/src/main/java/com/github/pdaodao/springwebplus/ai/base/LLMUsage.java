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
}
