package com.github.pdaodao.springwebplus.ai;

import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import java.util.List;

/**
 * 向量存储查询
 */
public interface AiVectorStore {
    /**
     * 保存或者更新
     * @param documents
     * @throws Exception
     */
    void save(List<AiEmbedText> documents) throws Exception;

    /**
     * 查询
     * @param query
     * @return
     * @throws Exception
     */
    List<AiEmbedText> query(AiEmbedTextQuery query) throws Exception;

    /**
     * 总行数
     * @param query
     * @return
     * @throws Exception
     */
    Long deleteByQuery(AiEmbedTextQuery query) throws Exception;

    long count() throws Exception;
}