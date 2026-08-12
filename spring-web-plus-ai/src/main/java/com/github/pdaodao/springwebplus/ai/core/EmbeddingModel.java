package com.github.pdaodao.springwebplus.ai.core;

import cn.hutool.core.collection.CollUtil;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface EmbeddingModel {

    float[] embed(String text);

    default List<float[]> embed(final List<String> texts) {
        Assert.notNull(texts, "Texts must not be null");
        if(CollUtil.isEmpty(texts)){
            return Collections.emptyList();
        }
        final List<float[]> rets = new ArrayList<>();
        for(final String t: texts){
            final float[] ff = embed(t);
            rets.add(ff);
        }
        return rets;
    }
}
