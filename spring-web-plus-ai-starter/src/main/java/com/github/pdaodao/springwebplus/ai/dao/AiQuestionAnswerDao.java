package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiQuestionAnswer;
import com.github.pdaodao.springwebplus.ai.mapper.AiQuestionAnswerMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AiQuestionAnswerDao extends BaseDao<AiQuestionAnswerMapper, AiQuestionAnswer> {

    public Boolean checkDistinctTitle(final String teamId, final String id, final String title) {
        return count(QueryBuilder.lambda(AiQuestionAnswer.class)
                .neq(AiQuestionAnswer::getId, id)
                .eq(AiQuestionAnswer::getTeamId, teamId)
                .eq(AiQuestionAnswer::getTitle, title)
                .build()) < 1;
    }

    public List<AiQuestionAnswer> infoList(final String teamId, final String q){
        return list(QueryBuilder.lambda(AiQuestionAnswer.class)
                .eq(AiQuestionAnswer::getTeamId,teamId)
                .like(q, AiQuestionAnswer::getTitle, AiQuestionAnswer::getAnswer)
                .build());
    }

    @Override
    protected void saveCheck(AiQuestionAnswer entity, boolean isInsert) {
        Boolean valid = checkDistinctTitle(entity.getTeamId(), entity.getId(), entity.getTitle());
        Preconditions.checkArgument(valid, "重复:"+entity.getTitle());
    }
}