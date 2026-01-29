package com.github.pdaodao.springwebplus.ai.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.store.VectorCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.*;

@Slf4j
public class OpenAiEmbedding implements AiEmbedding {
    private final String baseUrl;
    private final String apiKey;

    private final String model;

    private final int batchSize;

    private OpenAiEmbeddingModel embeddingModel;
    private int dimension = 1024;

    private VectorCache vectorCache;

    public OpenAiEmbedding(String baseUrl, String apiKey, String model, int dimension, int batchSize) {
        if(StrUtil.isBlank(apiKey)){
            apiKey = "123";
        }
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.dimension = dimension;
        this.batchSize = batchSize;
        this.vectorCache = new VectorCache();
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

    @Override
    public void putCache(String key, float[] embed) {
        vectorCache.put(key, embed);
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
            embeddingModel = new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED,
                    OpenAiEmbeddingOptions.builder().dimensions(dimension).model(model).build());
        }
        return embeddingModel;
    }

    @Override
    public float[] embed(String text) {
        return model().embed(text);
    }

    @Override
    public List<float[]> embed(final List<String> texts) {
        if (CollUtil.isEmpty(texts)) {
            return ListUtil.empty();
        }
        final Map<String, float[]> map = new HashMap<>();
        final Set<String> toProcessed = new HashSet<>();
        for(final String t: texts){
            final float[] ft = vectorCache.get(t);
            if(ArrayUtil.isNotEmpty(ft)){
                map.put(StrUtil.trim(t), ft);
            }else{
                toProcessed.add(StrUtil.trim(t));
            }
        }
        final List<List<String>> sps = CollUtil.split(toProcessed, batchSize);
        for (final List<String> sp : sps) {
            final List<float[]> batched = retry(sp);
            for(int i = 0; i < sp.size(); i++){
                final String k = sp.get(i);
                final float[] ft = batched.get(i);
                vectorCache.put(k, ft);
                map.put(k, ft);
            }
        }
        final List<float[]> ret = new ArrayList<>();
        for(final String t: texts){
            if(StrUtil.isBlank(t)){
                ret.add(new float[0]);
            }else{
                ret.add(map.get(StrUtil.trim(t)));
            }
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
