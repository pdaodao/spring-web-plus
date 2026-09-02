package com.github.pdaodao.springwebplus.ai.util;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.entity.AiSkill;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.agentscope.core.skill.AgentSkill;

public class AiSkillUtil {
    private static final int MAX_SKILL_NAME_LENGTH = 255;

    public static void validateSkillName(String skillName) {
        Preconditions.checkNotBlank(skillName, "Skill name cannot be null or empty");
        Preconditions.assertTrue(StrUtil.length(skillName) > MAX_SKILL_NAME_LENGTH, "Skill name cannot exceed " + MAX_SKILL_NAME_LENGTH + " characters");
        Preconditions.assertTrue(skillName.contains("..") || skillName.contains("/") || skillName.contains("\\"), "Skill name cannot contain path separators or '..'");
    }

    public static AgentSkill toSkill(final AiSkill sk){
        if(sk == null){
            return null;
        }
        final AgentSkill ret = AgentSkill.builder()
                .name(sk.getName())
                .description(sk.getDescription())
                .skillContent(sk.getSkillContent())
                .source(sk.getSource())
                .metadata(sk.getMetadata())
                .resources(sk.getResources())
                .build();
        return ret;
    }
}
