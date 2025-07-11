package com.github.pdaodao.springwebplus.dao;

import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysUser;
import com.github.pdaodao.springwebplus.mapper.SysUserMapper;
import com.github.pdaodao.springwebplus.query.SysUserQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SysUserDao extends BaseDao<SysUserMapper, SysUser> {

    public SysUser infoWithRole(final String id, final String username) {
        final SysUserQuery query = new SysUserQuery();
        query.setId(id);
        query.setUsername(username);
        return baseMapper.infoWithRole(query);
    }

    public List<SysUser> list(final SysUserQuery query) {
        return list(QueryBuilder.lambda(SysUser.class)
                .like(query.getUsername(), SysUser::getUsername, SysUser::getNickname)
                .selectExclude("password", "salt").build());
    }
}
