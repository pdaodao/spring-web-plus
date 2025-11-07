package com.github.pdaodao.springwebplus.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pdaodao.springwebplus.base.entity.WithVersion;

public abstract class BaseDaoWithVersion<M extends BaseMapper<T>, T extends WithVersion> extends BaseDao{

}