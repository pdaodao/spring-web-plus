package com.github.pdaodao.springwebplus.ai.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "大模型返回内容")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LLMResponse {
    @Schema(description = "回答id")
    private String id;

    @Schema(description = "返回内容块")
    private List<MsgBlock> blocks;

    @Schema(description = "使用量")
    private LLMUsage usage;

    @Schema(description = "耗时ms")
    private Long cost;

    public static LLMResponse of(){
        return new LLMResponse();
    }

    public void addBlock(final MsgBlock block){
        if(block == null){
            return;
        }
        if(blocks == null){
            blocks = new ArrayList<>();
        }
        blocks.add(block);
    }

    public MsgBlock addTextBlock(final String text){
        final MsgBlock block = MsgBlock.ofText(text);
        addBlock(block);
        return block;
    }
}
