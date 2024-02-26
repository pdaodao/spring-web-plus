package com.github.apengda.springwebplus.starter.dao;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.apengda.springwebplus.starter.entity.Entity;
import com.github.apengda.springwebplus.starter.pojo.PageR;
import com.github.apengda.springwebplus.starter.util.PageHelper;
import com.github.apengda.springwebplus.starter.util.RequestUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class BaseDao<M extends BaseMapper<T>, T extends Entity> extends ServiceImpl<M, T> {
    /**
     * 在保存之前进行检查 有异常抛出
     *
     * @param entity
     * @param isInsert 是否是插入 否则为更新
     */
    void saveCheck(T entity, boolean isInsert) {

    }

    /**
     * 保存或者更新 当主键不存在 或者数据不存在时 插入数据 否则更新
     *
     * @param entity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(T entity) {
        if (ObjectUtil.isNull(entity.getId())) {
            saveCheck(entity, true);
            return super.save(entity);
        }
        final T old = getById(entity.getId());
        if (ObjectUtil.isNull(old)) {
            saveCheck(entity, true);
            return super.save(entity);
        }
        saveCheck(entity, false);
        return updateById(entity);
    }

    /**
     * 分页查询
     *
     * @param queryWrapper
     * @return
     */
    public PageR<T> page(final Wrapper<T> queryWrapper) {
        try (final PageHelper pageHelper = PageHelper.startPage(RequestUtil.getPageParam())) {
            final List<T> list = list(queryWrapper);
            return pageHelper.toPageResult(list);
        }
    }

}