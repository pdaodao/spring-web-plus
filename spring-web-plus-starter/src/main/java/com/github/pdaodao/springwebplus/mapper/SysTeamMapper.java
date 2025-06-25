package com.github.pdaodao.springwebplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pdaodao.springwebplus.entity.SysTeam;
import com.github.pdaodao.springwebplus.entity.SysTeamUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysTeamMapper extends BaseMapper<SysTeam> {

    List<SysTeam> userTeams(@Param("userId") final String userId);

    List<SysTeamUser> teamUsers(@Param("teamId") final String teamId, @Param("q") final String q);
}
