package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiChatSessionMsg;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatSessionMsgMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiChatSessionMsgDao extends BaseDao<AiChatSessionMsgMapper, AiChatSessionMsg> {

    public List<AiChatSessionMsg> listBySession(final String sessionId){
        Preconditions.checkNotBlank(sessionId, "session id is null.");
        return list(QueryBuilder.lambda(AiChatSessionMsg.class)
                .eq(AiChatSessionMsg::getSessionId, sessionId)
                .build());
    }
}