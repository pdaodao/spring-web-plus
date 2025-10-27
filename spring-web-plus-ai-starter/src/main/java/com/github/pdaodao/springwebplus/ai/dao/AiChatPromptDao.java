package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiChatPrompt;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatPromptMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = "chatPrompt")
public class AiChatPromptDao extends BaseDao<AiChatPromptMapper, AiChatPrompt> {
    @Cacheable(key = "#p0.id", condition = "#p0.id != null")
    public AiChatPrompt detail(final String id){
        return getById(id);
    }

    @Override
    @CacheEvict(key = "#p0.id", condition = "#p0.id != null")
    public boolean save(AiChatPrompt entity) {
        return super.save(entity);
    }
}