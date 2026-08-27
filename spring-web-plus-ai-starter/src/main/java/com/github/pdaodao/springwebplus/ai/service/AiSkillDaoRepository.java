package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import io.agentscope.core.skill.AgentSkill;
import io.agentscope.core.skill.repository.AgentSkillRepository;
import io.agentscope.core.skill.repository.AgentSkillRepositoryInfo;
import java.util.List;

public class AiSkillDaoRepository implements AgentSkillRepository {
    private final AiChatContext aiChatContext;

    public AiSkillDaoRepository(AiChatContext aiChatContext) {
        this.aiChatContext = aiChatContext;
    }

    @Override
    public AgentSkill getSkill(String name) {
        return null;
    }

    @Override
    public List<String> getAllSkillNames() {
        return null;
    }

    @Override
    public List<AgentSkill> getAllSkills() {
        return null;
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
        return false;
    }

    @Override
    public AgentSkillRepositoryInfo getRepositoryInfo() {
        return null;
    }

    @Override
    public String getSource() {
        return null;
    }

    @Override
    public void setWriteable(boolean writeable) {

    }

    @Override
    public boolean isWriteable() {
        return false;
    }
}