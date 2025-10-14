package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiChatDoc;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatDocMapper;
import com.github.pdaodao.springwebplus.ai.query.AiChatDocQuery;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiChatDocDao extends BaseDao<AiChatDocMapper, AiChatDoc> {

    public List<AiChatDoc> list(final AiChatDocQuery query) {
        return list(QueryBuilder.lambda(AiChatDoc.class)
                .eq(AiChatDoc::getTeamId, query.getTeamId())
                .eq(AiChatDoc::getIsDir, query.getIsDir())
                .selectExclude("sql_text").build());
    }
}