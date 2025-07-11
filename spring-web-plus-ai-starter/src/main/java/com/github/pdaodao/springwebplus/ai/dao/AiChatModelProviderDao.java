package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.base.ChatModelType;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModelProvider;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatModelProviderMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Cacheable(cacheNames = "AiChatModelProvider")
public class AiChatModelProviderDao extends BaseDao<AiChatModelProviderMapper, AiChatModelProvider> {
    public List<AiChatModelProvider> list(final ChatModelType type){
        return list(QueryBuilder.lambda(AiChatModelProvider.class)
                .eq(AiChatModelProvider::getType, type).build());
    }
}
