package com.github.pdaodao.springwebplus.base.dao;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.pdaodao.springwebplus.base.entity.*;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.data.PageResult;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.pojo.EntityDiffWrap;
import org.apache.ibatis.ognl.OgnlOps;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseDao<M extends BaseMapper<T>, T extends Entity> extends ServiceImpl<M, T> {
    /**
     * 在保存之前进行检查 有异常抛出
     *
     * @param entity
     * @param isInsert 是否是插入 否则为更新
     */
    protected void saveCheck(T entity, boolean isInsert) {

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
        if(ObjectUtil.isNull(entity.getId()) && entity instanceof AutoIdEntity && ObjectUtil.equals(0l, entity.getId())){
            entity.setId(null);
        }
        if (ObjectUtil.isNull(entity.getId())) {
            saveCheck(entity, true);
            processTeamProject(entity);
            boolean ret = super.save(entity);
            afterInsert(entity);
            return ret;
        }
        final TableInfo tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
        T old = getOne(new QueryWrapper<T>().eq(tableInfo.getKeyColumn(), entity.getId()));
        if(old != null && tableInfo.isWithLogicDelete() && old instanceof WithDelete){
            if(BooleanUtil.isTrue(((WithDelete) old).getIsDeleted())){
                remove(new QueryWrapper<T>().eq(tableInfo.getKeyColumn(), entity.getId()));
                old = null;
            }
        }
        if (ObjectUtil.isNull(old)) {
            saveCheck(entity, true);
            processTeamProject(entity);
            final boolean ret = super.save(entity);
            afterInsert(entity);
            return ret;
        }

        saveCheck(entity, false);
        return updateById(entity);
    }

    protected void afterInsert(T entity){

    }

    private void processTeamProject(final T entity){
        if(entity == null){
            return;
        }
        if(entity instanceof WithDelete){
            ((WithDelete) entity).setIsDeleted(false);
        }
        if(entity instanceof WithTeam){
            final WithTeam withTeam = (WithTeam) entity;
            if(StrUtil.isBlank(withTeam.getTeamId())){
                withTeam.setTeamId(RequestUtil.getTeamOrDefault());
            }
        }
        if(entity instanceof WithProject){
            final WithProject p = (WithProject) entity;
            p.setProjectId(RequestUtil.getProjectIdOrDefault());
        }
    }

    /**
     * 分页查询
     *
     * @param queryWrapper
     * @return
     */
    public PageResult<T> listPage(final Wrapper<T> queryWrapper) {
        try (final PageHelper pageHelper = PageHelper.startPage(RequestUtil.getPageParam())) {
            final List<T> list = list(queryWrapper);
            return pageHelper.toPageResult(list);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveDiffListById(final List<T> list, final List<T> oldList) {
        if(CollUtil.isEmpty(list)){
            return false;
        }
        final EntityDiffWrap<T> diffWrap = BeanUtils.diff(list, oldList, Entity::getId);
        if(CollUtil.isNotEmpty(diffWrap.getToDeletes())){
            removeBatchByIds(diffWrap.getToDeletes().stream().map(t -> t.getId()).collect(Collectors.toList()));
        }
        if(CollUtil.isNotEmpty(diffWrap.getToUpdates())){
            updateBatchById(diffWrap.getToUpdates());
        }
        if(CollUtil.isNotEmpty(diffWrap.getToInserts())){
            saveBatch(diffWrap.getToInserts());
        }
        return true;
    }

}