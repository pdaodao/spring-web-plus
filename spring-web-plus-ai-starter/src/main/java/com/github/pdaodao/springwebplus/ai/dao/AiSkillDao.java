package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.entity.AiSkill;
import com.github.pdaodao.springwebplus.ai.mapper.AiSkillMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiSkillDao extends BaseDao<AiSkillMapper, AiSkill> {

    public AiSkill byName(final String name, final String teamId){
        Preconditions.checkNotBlank(name, "skill name is empty.");
        return getOne(QueryBuilder.lambda(AiSkill.class)
                .eq(AiSkill::getName, name)
                .eq(AiSkill::getTeamId, teamId).build());
    }

    public List<AiSkill> list(final String teamId){
        return list(QueryBuilder.lambda(AiSkill.class)
                .eq(AiSkill::getTeamId, teamId).build());
    }
}