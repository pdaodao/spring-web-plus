package com.github.pdaodao.springwebplus.dao;

import com.github.pdaodao.springwebplus.entity.SysUser;
import com.github.pdaodao.springwebplus.mapper.SysUserMapper;
import com.github.pdaodao.springwebplus.query.SysUserQuery;
import com.github.pdaodao.springwebplus.starter.dao.BaseDao;
import com.github.pdaodao.springwebplus.starter.query.QueryBuilder;
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
