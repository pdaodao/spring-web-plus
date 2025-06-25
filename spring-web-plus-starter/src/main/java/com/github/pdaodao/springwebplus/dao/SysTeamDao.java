package com.github.pdaodao.springwebplus.dao;

import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.pojo.MemberType;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.entity.SysTeam;
import com.github.pdaodao.springwebplus.entity.SysTeamUser;
import com.github.pdaodao.springwebplus.mapper.SysTeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@CacheConfig(cacheNames = "SysTeam")
public class SysTeamDao extends BaseDao<SysTeamMapper, SysTeam> {
    @Autowired
    private SysTeamUserDao teamUserDao;

    @Cacheable(key = "#p0", condition = "#p0 != null")
    public List<SysTeam> userTeams(final String userId){
        final List<SysTeam> list = baseMapper.userTeams(userId);
        return list;
    }

    public SysTeamUserDao teamUserDao(){
        return teamUserDao;
    }

    @CacheEvict(key = "#p0", condition = "#p0 != null")
    public void userTeamsClear(final String userId){

    }

    @Override
    protected void afterInsert(SysTeam entity) {
        final String userId = RequestUtil.getUserId();
        if(userId == null){
            return;
        }
        final SysTeamUser tu = new SysTeamUser();
        tu.setTeamId(entity.getId());
        tu.setUserId(userId);
        tu.setMemberType(MemberType.creator);
        teamUserDao.save(tu);
    }

    public List<SysTeamUser> teamUsers(final String teamId, final String q){
        return baseMapper.teamUsers(teamId, QueryBuilder.likeValue(q));
    }
}
