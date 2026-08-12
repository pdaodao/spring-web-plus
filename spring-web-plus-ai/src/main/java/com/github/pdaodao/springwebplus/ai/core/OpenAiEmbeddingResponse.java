package com.github.pdaodao.springwebplus.ai.core;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OpenAiEmbeddingResponse {
    private String object;
    private List<EmbeddingData> data;

    @Data
    public static class EmbeddingData{
        private String object;
        private float[] embedding;
    }
}
