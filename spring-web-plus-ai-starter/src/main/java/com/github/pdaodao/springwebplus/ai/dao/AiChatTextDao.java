package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiChatText;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatTextMapper;
import com.github.pdaodao.springwebplus.ai.query.AiChatTextQuery;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AiChatTextDao extends BaseDao<AiChatTextMapper, AiChatText> {

    public Boolean checkDistinctTitle(final String teamId, final String id, final String title, final String topicId) {
        return count(QueryBuilder.lambda(AiChatText.class)
                .neq(AiChatText::getId, id)
                .eq(AiChatText::getTeamId, teamId)
                .eq(AiChatText::getTitle, title)
                .eq(AiChatText::getTopicId, topicId)
                .build()) < 1;
    }

    public long countByTopic(final String topicId){
        return count(QueryBuilder.lambda(AiChatText.class)
                .eq(AiChatText::getTopicId, topicId).build());
    }

    public List<AiChatText> infoList(final AiChatTextQuery query){
        return list(QueryBuilder.lambda(AiChatText.class)
                .eq(AiChatText::getTeamId, query.getTeamId())
                .eq(AiChatText::getNamespace, query.getNamespace())
                .eq(AiChatText::getTopicId, query.getTopicId())
                .eq(AiChatText::getFileId, query.getFileId())
                .like(query.getQ(), AiChatText::getTitle, AiChatText::getContent)
                .build());
    }

    @Override
    protected void saveCheck(AiChatText entity, boolean isInsert) {
        Boolean valid = checkDistinctTitle(entity.getTeamId(), entity.getId(), entity.getTitle(), entity.getTopicId());
        Preconditions.checkArgument(valid, "数据的重复");
    }
}
