package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiChatTermText;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatTermTextMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiChatTermTextDao extends BaseDao<AiChatTermTextMapper, AiChatTermText> {

    public Boolean checkDistinctTitle(final String teamId, final String id, final String title) {
        return count(QueryBuilder.lambda(AiChatTermText.class)
                .neq(AiChatTermText::getId, id)
                .eq(AiChatTermText::getTeamId, teamId)
                .eq(AiChatTermText::getTitle, title)
                .build()) < 1;
    }

    public List<AiChatTermText> infoList(final String teamId, final String q){
        return list(QueryBuilder.lambda(AiChatTermText.class)
                .eq(AiChatTermText::getTeamId,teamId)
                .like(q, AiChatTermText::getTitle, AiChatTermText::getRemark, AiChatTermText::getSubs)
                .build());
    }

    @Override
    protected void saveCheck(AiChatTermText entity, boolean isInsert) {
        Boolean valid = checkDistinctTitle(entity.getTeamId(), entity.getId(), entity.getTitle());
        Preconditions.checkArgument(valid, "名称重复");
    }
}
