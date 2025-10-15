package com.github.pdaodao.springwebplus.ai;

import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import java.util.List;

public interface AiVectorStore {
    void add(List<AiEmbedText> documents) throws Exception;

    List<AiEmbedText> query(AiEmbedTextQuery query) throws Exception;

    Long deleteByQuery(AiEmbedTextQuery query) throws Exception;

    long count() throws Exception;
}