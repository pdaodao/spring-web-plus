package com.github.pdaodao.springwebplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pdaodao.springwebplus.entity.SysTeam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysTeamMapper extends BaseMapper<SysTeam> {

    List<SysTeam> userTeams(@Param("userId") final String userId, @Param("permissionNamespace") final String permissionNamespace);
}
