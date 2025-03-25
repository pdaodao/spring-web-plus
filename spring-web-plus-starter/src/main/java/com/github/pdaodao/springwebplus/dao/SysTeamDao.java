package com.github.pdaodao.springwebplus.dao;

import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.entity.SysTeam;
import com.github.pdaodao.springwebplus.mapper.SysTeamMapper;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SysTeamDao extends BaseDao<SysTeamMapper, SysTeam> {

    public List<SysTeam> userTeams(final String userId, final String permissionNamespace){
        final List<SysTeam> list = baseMapper.userTeams(userId, permissionNamespace);
        return list;
    }
}
