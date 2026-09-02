package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.dao.AiSkillDao;
import com.github.pdaodao.springwebplus.ai.entity.AiSkill;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AiSkillService {
    private final AiSkillDao dao;

    public AiSkill byName(final String name, final String teamId){
        return dao.byName(name, teamId);
    }

    public List<AiSkill> list(final String teamId){
        return dao.list(teamId);
    }
}
