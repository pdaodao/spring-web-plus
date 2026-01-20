package com.github.pdaodao.springwebplus.ai.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OpenAiEmbedding implements AiEmbedding {
    private final String baseUrl;
    private final String apiKey;

    private final String model;

    private final int batchSize;

    private OpenAiEmbeddingModel embeddingModel;
    private int dimension = 0;

    public OpenAiEmbedding(String baseUrl, String apiKey, String model, int batchSize) {
        if(StrUtil.isBlank(apiKey)){
            apiKey = "123";
        }
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.batchSize = batchSize;
    }

    @Override
    public int dimension() {
        if (dimension > 0) {
            return dimension;
        }
        final float[] f = model().embed("北京欢迎您.");
        dimension = f.length;
        return dimension;
    }

    private OpenAiEmbeddingModel model() {
        if (embeddingModel != null) {
            return embeddingModel;
        }
        synchronized (this) {
            final OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(baseUrl)
                    .apiKey(apiKey)
                    .build();
            embeddingModel = new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED, OpenAiEmbeddingOptions.builder()
                    .model(model).build());
        }
        return embeddingModel;
    }

    @Override
    public float[] embed(String text) {
        return model().embed(text);
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        if (CollUtil.isEmpty(texts)) {
            return ListUtil.empty();
        }
        final List<float[]> ret = new ArrayList<>();
        final List<List<String>> sps = CollUtil.split(texts, batchSize);
        for (final List<String> sp : sps) {
            final List<float[]> batched = retry(sp);
            ret.addAll(batched);

        }
        return ret;
    }

    private List<float[]> retry(final List<String> sp){
        if(CollUtil.isEmpty(sp)){
            return ListUtil.empty();
        }
        for(int i = 0; i < 3; i++){
            try{
                return model().embed(sp);
            }catch (Exception e){
                log.error(e.getMessage(), e);
                if(i == 2){
                    throw e;
                }
                ThreadUtil.sleep(3);
            }
        }
        return ListUtil.empty();
    }
}
