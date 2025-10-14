package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiChatDocItem;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatDocItemMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiChatDocItemDao extends BaseDao<AiChatDocItemMapper, AiChatDocItem> {
    public long countByDocId(final String docId) {
        return count(QueryBuilder.lambda(AiChatDocItem.class)
                .eq(AiChatDocItem::getDocId, docId).build());
    }

    public List<AiChatDocItem> listByDocId(final String docId) {
        return list(QueryBuilder.lambda(AiChatDocItem.class)
                .eq(AiChatDocItem::getDocId, docId).build());
    }
}
