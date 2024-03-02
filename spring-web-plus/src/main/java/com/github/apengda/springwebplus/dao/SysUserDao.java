package com.github.apengda.springwebplus.dao;

import com.github.apengda.springwebplus.entity.SysUser;
import com.github.apengda.springwebplus.mapper.SysUserMapper;
import com.github.apengda.springwebplus.query.SysUserQuery;
import com.github.apengda.springwebplus.starter.dao.BaseDao;
import com.github.apengda.springwebplus.starter.query.QueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SysUserDao extends BaseDao<SysUserMapper, SysUser> {

    public SysUser infoWithRole(final String id, final String username) {
        return baseMapper.infoWithRole(id, username);
    }

    public List<SysUser> list(final SysUserQuery query) {
        return list(QueryBuilder.lambda(SysUser.class)
                .like(query.getUsername(), SysUser::getUsername, SysUser::getNickname)
                .selectExcludes("password", "salt").build());
    }
}
