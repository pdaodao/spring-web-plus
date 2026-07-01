package com.github.pdaodao.springwebplus.service;

import cn.hutool.core.util.RandomUtil;
import com.github.pdaodao.springwebplus.dao.SysRoleDao;
import com.github.pdaodao.springwebplus.dao.SysUserDao;
import com.github.pdaodao.springwebplus.dao.SysUserRoleDao;
import com.github.pdaodao.springwebplus.entity.SysRole;
import com.github.pdaodao.springwebplus.entity.SysUser;
import com.github.pdaodao.springwebplus.entity.SysUserRole;
import com.github.pdaodao.springwebplus.pojo.UserPassword;
import com.github.pdaodao.springwebplus.query.SysUserQuery;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SysUserService {
    private final SysUserDao sysUserDao;
    private final SysRoleDao roleDao;
    private final SysUserRoleDao userRoleDao;

    public SysUser infoWithRole(final String id, final String username) {
        return sysUserDao.infoWithRole(id, username);
    }

    /**
     * 添加/修改 用户
     *
     * @param sysUser
     * @return
     */
    public boolean saveUser(final SysUser sysUser) {
        if (sysUser.getId() == null) {
            Preconditions.checkNotBlank(sysUser.getPassword(), "密码为空.");
            final String salt = RandomUtil.randomString(6);
            sysUser.setSalt(salt);
            String password = PasswordUtil.encrypt(sysUser.getPassword(), salt);
            sysUser.setPassword(password);
        } else {
            sysUser.setPassword(null);
        }
        sysUserDao.save(sysUser);
        return true;
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    public boolean deleteById(final String id) {
        return sysUserDao.removeById(id);
    }

    /**
     * 用户信息
     *
     * @param id
     * @return
     */
    public SysUser getById(final String id) {
        return sysUserDao.getById(id);
    }

    public List<SysUser> list(final SysUserQuery query) {
        return sysUserDao.list(query);
    }

    /**
     * 修改密码
     *
     * @param sysUser
     * @return
     */
    public boolean updatePassword(final String userId, final String password) {
        return sysUserDao.updatePassword(userId, password);
    }

    public List<SysRole> userRoles(final String userId){
        final List<SysUserRole> rs = userRoleDao.userRoles(userId);
        final List<SysRole> list = new ArrayList<>();
        for(final SysUserRole r: rs){
            final SysRole role = roleDao.info(r.getRoleId());
            list.add(role);
        }
        return list;
    }
}
