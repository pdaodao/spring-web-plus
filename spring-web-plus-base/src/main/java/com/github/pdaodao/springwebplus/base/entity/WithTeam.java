package com.github.pdaodao.springwebplus.base.entity;

import com.github.pdaodao.springwebplus.base.util.RequestUtil;

/**
 * 团队隔离
 */
public interface WithTeam {
    String getTeamId();

    void setTeamId(String teamId);
}
