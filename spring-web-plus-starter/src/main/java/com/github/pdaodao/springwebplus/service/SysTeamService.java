package com.github.pdaodao.springwebplus.service;

import com.github.pdaodao.springwebplus.dao.SysTeamDao;
import com.github.pdaodao.springwebplus.entity.SysTeam;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SysTeamService {
    private SysTeamDao sysTeamDao;
    public List<SysTeam> userTeams(final String userId, final String permissionNamespace){
        return sysTeamDao.userTeams(userId, permissionNamespace);
    }

    public SysTeam save(final SysTeam sysTeam){
        sysTeamDao.save(sysTeam);
        return sysTeam;
    }
}
