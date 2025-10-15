package com.github.pdaodao.springwebplus.ai.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.util.ChatModelUtil;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import java.util.ArrayList;
import java.util.List;

public class OpenAiEmbedding implements AiEmbedding {
    private final String baseUrl;
    private final String apiKey;

    private OpenAiEmbeddingModel embeddingModel;
    private int dimension = 0;

    public OpenAiEmbedding(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public int dimension() {
        if(dimension > 0){
            return dimension;
        }
        final float[] f = model().embed("北京欢迎您.");
        dimension = f.length;
        return dimension;
    }

    private OpenAiEmbeddingModel model(){
        if(embeddingModel != null){
            return embeddingModel;
        }
        synchronized (this){
            embeddingModel = new OpenAiEmbeddingModel(ChatModelUtil.openAiApi(baseUrl, apiKey));
        }
        return embeddingModel;
    }

    @Override
    public float[] embed(String text) {
        return model().embed(text);
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        if(CollUtil.isEmpty(texts)){
            return ListUtil.empty();
        }
        final List<float[]> ret = new ArrayList<>();
        final List<List<String>> sps = CollUtil.split(texts, 15);
        for(final List<String> sp: sps){
            final List<float[]> batched = embeddingModel.embed(sp);
            ret.addAll(batched);
        }
        return ret;
    }
}
