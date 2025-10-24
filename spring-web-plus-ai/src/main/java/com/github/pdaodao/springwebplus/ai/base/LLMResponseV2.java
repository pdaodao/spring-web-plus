package com.github.pdaodao.springwebplus.ai.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
@Schema(description = "大模型返回内容")
public class LLMResponseV2 {
    private List<LLMMsg> contentBlocks;


    @Schema(description = "使用量")
    private LLMUsage usage;
}
