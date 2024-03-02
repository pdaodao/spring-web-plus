package com.github.apengda.springwebplus.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.apengda.springwebplus.entity.SysUser;
import com.github.apengda.springwebplus.mapper.SysUserMapper;
import com.github.apengda.springwebplus.starter.dao.BaseDao;
import com.github.apengda.springwebplus.starter.util.Preconditions;
import org.springframework.stereotype.Component;

@Component
public class SysUserDao extends BaseDao<SysUserMapper, SysUser> {

    public SysUser byUsername(final String username) {
        Preconditions.checkNotBlank(username, "username is null.");
        return getOne(Wrappers.lambdaQuery(SysUser.class)
                .eq(SysUser::getUsername, username));
    }

    public void addUser(final SysUser sysUser) {

    }
}
