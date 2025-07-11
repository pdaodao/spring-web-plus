package com.github.pdaodao.springwebplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pdaodao.springwebplus.entity.SysUser;
import com.github.pdaodao.springwebplus.query.SysUserQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    List<SysUser> infoWithRole(final SysUserQuery query);
}
