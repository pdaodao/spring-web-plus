package com.github.apengda.springwebplus.service;

import com.github.apengda.springwebplus.dao.SysUserDao;
import com.github.apengda.springwebplus.entity.SysUser;
import com.github.apengda.springwebplus.query.SysUserQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SysUserService {
    private final SysUserDao sysUserDao;

    public SysUser infoWithRole(final String id, final String username) {
        return sysUserDao.infoWithRole(id, username);
    }

    /**
     * 添加/修改 用户
     *
     * @param sysUser
     * @return
     */
    public boolean saveUser(SysUser sysUser) {
        sysUser.setPassword(null);
        sysUser.setSalt(null);
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
    public boolean updatePassword(final SysUser sysUser) {


        return true;
    }
}
