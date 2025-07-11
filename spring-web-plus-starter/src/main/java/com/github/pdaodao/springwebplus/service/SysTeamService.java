package com.github.pdaodao.springwebplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.dao.SysTeamDao;
import com.github.pdaodao.springwebplus.entity.SysTeam;
import com.github.pdaodao.springwebplus.entity.SysTeamUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Struct;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SysTeamService {
    private SysTeamDao sysTeamDao;

    public List<SysTeam> userTeams(final String userId){
        List<SysTeam> list = sysTeamDao.userTeams(userId);
        if(CollUtil.isEmpty(list)){
            final SysTeam sysTeam = new SysTeam();
            sysTeam.setId(userId);
            sysTeam.setTitle("个人空间");
            sysTeamDao.save(sysTeam);
            sysTeamDao.userTeamsClear(userId);
            list = sysTeamDao.userTeams(userId);
        }
        return list;
    }

    public SysTeam save(final SysTeam sysTeam){
        sysTeamDao.save(sysTeam);
        return sysTeam;
    }

    public List<SysTeam> list(final String q){
        return sysTeamDao.list(QueryBuilder.lambda(SysTeam.class).like(q, SysTeam::getTitle).build());
    }


    public List<SysTeamUser> teamUsers(final String teamId, final String q){
        return sysTeamDao.teamUsers(teamId, q);
    }

    public Boolean deleteById(final String id){
        return sysTeamDao.removeById(id);
    }

    public SysTeamUser addTeamUser(final SysTeamUser sysTeamUser){
        final List<SysTeam> teams = sysTeamDao.userTeams(sysTeamUser.getUserId());
        final Optional<SysTeam> optionalSysTeam = teams.stream().filter(t -> ObjectUtil.equals(t.getId(), sysTeamUser.getTeamId())).findFirst();
        if(optionalSysTeam.isPresent()){
            sysTeamUser.setId(optionalSysTeam.get().getId());
        }
        sysTeamDao.teamUserDao().save(sysTeamUser);
        sysTeamDao.userTeamsClear(sysTeamUser.getUserId());
        return sysTeamUser;
    }

    public Boolean removeTeamUser(final String id){
        return sysTeamDao.teamUserDao().removeById(id);
    }
}
