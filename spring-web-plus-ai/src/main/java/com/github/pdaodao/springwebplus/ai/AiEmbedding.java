package com.github.pdaodao.springwebplus.ai;

import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.util.List;

public interface AiEmbedding {
    /**
     * 维度
     *
     * @return
     */
    int dimension();


    void putCache(final String key, float[] embed);


    default float[] embed(final String text) {
        Preconditions.checkNotNull(text, "Text must not be null");
        List<float[]> response = this.embed(List.of(text));
        return (float[]) response.iterator().next();
    }

    List<float[]> embed(final List<String> texts);
}