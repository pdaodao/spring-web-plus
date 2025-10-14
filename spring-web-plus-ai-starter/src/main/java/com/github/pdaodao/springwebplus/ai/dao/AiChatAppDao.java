package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatAppMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import org.springframework.stereotype.Component;

@Component
public class AiChatAppDao extends BaseDao<AiChatAppMapper, AiChatApp> {
    public AiChatApp info(final String id) {
        return null;
    }
}