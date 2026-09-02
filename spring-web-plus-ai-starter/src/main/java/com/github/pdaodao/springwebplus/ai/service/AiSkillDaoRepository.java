package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.entity.AiSkill;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.util.AiSkillUtil;
import io.agentscope.core.skill.AgentSkill;
import io.agentscope.core.skill.repository.AgentSkillRepository;
import io.agentscope.core.skill.repository.AgentSkillRepositoryInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AiSkillDaoRepository implements AgentSkillRepository {
    private final AiSkillService skillService;
    private final AiChatContext aiChatContext;

    public AiSkillDaoRepository(AiSkillService skillService, AiChatContext aiChatContext) {
        this.skillService = skillService;
        this.aiChatContext = aiChatContext;
    }

    @Override
    public AgentSkill getSkill(String name) {
        AiSkillUtil.validateSkillName(name);
        final AiSkill aiSkill = skillService.byName(name, null);
        return AiSkillUtil.toSkill(aiSkill);
    }

    @Override
    public List<String> getAllSkillNames() {
        return skillService.list(null).stream().map(t -> t.getName()).collect(Collectors.toList());
    }

    @Override
    public List<AgentSkill> getAllSkills() {
        final List<AiSkill> sks = skillService.list(null);
        final List<AgentSkill> rets = new ArrayList<>();
        for(final AiSkill sk: sks){
            rets.add(AiSkillUtil.toSkill(sk));
        }
        return rets;
    }

    @Override
    public boolean save(List<AgentSkill> skills, boolean force) {
        return false;
    }

    @Override
    public boolean delete(String skillName) {
        return false;
    }

    @Override
    public boolean skillExists(String skillName) {
        final AiSkill sk = skillService.byName(skillName, null);
        return sk != null;
    }

    @Override
    public AgentSkillRepositoryInfo getRepositoryInfo() {
        return new AgentSkillRepositoryInfo("database", getSource(), false);
    }

    @Override
    public String getSource() {
        String teamId = "0";
        if(aiChatContext != null && aiChatContext.getReq() != null && aiChatContext.getReq().getTeamId() != null){
            teamId = aiChatContext.getReq().getTeamId();
        }
        return "database_"+teamId;
    }

    @Override
    public void setWriteable(boolean writeable) {

    }

    @Override
    public boolean isWriteable() {
        return false;
    }
}