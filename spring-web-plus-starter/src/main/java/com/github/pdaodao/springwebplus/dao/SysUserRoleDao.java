package com.github.pdaodao.springwebplus.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.entity.SysUserRole;
import com.github.pdaodao.springwebplus.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SysUserRoleDao extends BaseDao<SysUserRoleMapper, SysUserRole> {

    public List<SysUserRole> userRoles(final String userId){
        return list(Wrappers.lambdaQuery(SysUserRole.class)
                .eq(SysUserRole::getUserId, userId));
    }
}