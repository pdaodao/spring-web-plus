package com.github.pdaodao.aicompare.store;

import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.lang.ThreadContextClassLoader;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.spi.model.embedding.EmbeddingModelFactory;

import java.util.ArrayList;
import java.util.List;

public class DocEmbeddingUtil {
    private static EmbeddingModel embeddingModel;

    /**
     * 文本向量化
     *
     * @param doc
     * @return
     */
    public static float[] embedding(final String doc) {
        if (StrUtil.isBlank(doc)) {
            return null;
        }
        try(final ThreadContextClassLoader loader = ThreadContextClassLoader.of(EmbeddingModelFactory.class.getClassLoader())){
            final EmbeddingModel m = getEmbeddingModel();
            final Embedding embedding = m.embed(doc).content();
            return embedding.vector();
        }
    }

    public static List<Float> asList(float[] sp) {
        if(sp == null){
            return null;
        }
        final List<Float> list = new ArrayList(sp.length);
        for(final float f: sp){
            list.add(f);
        }
        return list;
    }


    /**
     * 获取文本向量化模型
     *
     * @return
     */
    private static EmbeddingModel getEmbeddingModel() {
        if (embeddingModel != null) {
            return embeddingModel;
        }
        synchronized (DocEmbeddingUtil.class) {
            if (embeddingModel != null) {
                return embeddingModel;
            }
            final EmbeddingModelFactory factory  = ServiceLoaderUtil.load(EmbeddingModelFactory.class,
                    DocEmbeddingUtil.class.getClassLoader()).iterator().next();
            Preconditions.checkNotNull(factory, "EmbeddingModelFactory不存在");
            embeddingModel = factory.create();
        }
        return embeddingModel;
    }
}
