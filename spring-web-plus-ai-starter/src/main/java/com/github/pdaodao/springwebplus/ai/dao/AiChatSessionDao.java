package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiChatSession;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatSessionMapper;
import com.github.pdaodao.springwebplus.ai.query.AiChatSessionQuery;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiChatSessionDao extends BaseDao<AiChatSessionMapper, AiChatSession> {

    public List<AiChatSession> list(final AiChatSessionQuery query) {
        return list(QueryBuilder.lambda(AiChatSession.class)
                .eq(AiChatSession::getObjId, query.getChatAppId())
                .eq(AiChatSession::getTeamId, query.getTeamId())
                .eq(AiChatSession::getUserId, query.getUserId())
                .ge(AiChatSession::getCreateTime, query.getStartDate())
                .le(AiChatSession::getCreateTime, query.getEndDate())
                .build().orderByDesc(AiChatSession::getId));
    }
}
