package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiChatKnowledgeChunk;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatKnowledgeChunkMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiChatKnowledgeChunkDao extends BaseDao<AiChatKnowledgeChunkMapper, AiChatKnowledgeChunk> {
    public long countByDocId(final String docId) {
        return count(QueryBuilder.lambda(AiChatKnowledgeChunk.class)
                .eq(AiChatKnowledgeChunk::getKnowledgeId, docId).build());
    }

    public List<AiChatKnowledgeChunk> listByDocId(final String docId) {
        return list(QueryBuilder.lambda(AiChatKnowledgeChunk.class)
                .eq(AiChatKnowledgeChunk::getKnowledgeId, docId).build());
    }

    public boolean removeByDocId(final String docId){
        Preconditions.checkNotBlank(docId, "remove doc-item docId is blank.");
        return remove(QueryBuilder.lambda(AiChatKnowledgeChunk.class)
                .eq(AiChatKnowledgeChunk::getKnowledgeId, docId).build());
    }
}
