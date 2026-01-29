package com.github.pdaodao.springwebplus.ai.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import java.util.ArrayList;
import java.util.List;

/**
 * 在存储和检索前进行向量化处理
 */
public class AiStoreEmbeddingUtil {

    public static void buildForSave(final AiEmbedText doc, final AiVectorStore vectorStore,
                                    final AiEmbedding aiEmbedding) throws Exception{
        if(doc == null || StrUtil.isBlank(doc.getContent())){
            return;
        }
        buildForSave(ListUtil.of(doc), vectorStore, aiEmbedding);
    }

    /**
     * 在保存数据前进行向量化
     * 1. 先检索数据 节省向量化资源
     * 2. 调用向量化模型
     * @param documents
     * @param aiEmbedding
     */
    public static void buildForSave(final List<AiEmbedText> documents, final AiVectorStore vectorStore,
                                    final AiEmbedding aiEmbedding) throws Exception{
        if(CollUtil.isEmpty(documents) || vectorStore == null || aiEmbedding == null){
            return;
        }
        //1. 检索久的向量数据进行缓存
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        for(final AiEmbedText t: documents){
            query.addNamespace(t.getNamespace());
            query.setTeamId(t.getTeamId());
            query.addTopic(t.getTopic());
            query.addDocId(t.getDocId());
            query.addType(t.getType());
            query.addId(t.getId());
        }
        query.setTopK(1000);
        query.setScore(0.0);
        final List<AiEmbedText> oldList = vectorStore.query(query);
        for(final AiEmbedText tt: oldList){
            aiEmbedding.putCache(tt.getContent(), tt.getEmbedding());
        }
        //2. 进行向量化
        final List<String> toProcessed = new ArrayList<>();
        for(final AiEmbedText t: documents){
            if(StrUtil.isNotBlank(t.getContent())){
                toProcessed.add(t.getContent());
            }
        }
        final List<float[]> fts = aiEmbedding.embed(toProcessed);
        int i = 0;
        for(final AiEmbedText t: documents){
            if(StrUtil.isNotBlank(t.getContent())){
                continue;
            }
            t.setEmbedding(fts.get(i++));
        }
    }


    /**
     * 构建查询时的向量化
     * @param query
     * @param aiEmbedding
     */
    public static void buildQuery(final AiEmbedTextQuery query, final AiEmbedding aiEmbedding){
        if(query == null || StrUtil.isBlank(query.getContent()) || aiEmbedding == null){
            return;
        }
        query.setEmbedding(aiEmbedding.embed(query.getContent()));
    }
}