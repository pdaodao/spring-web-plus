package com.github.pdaodao.springwebplus.task.dao;

import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskNodeEntity;
import com.github.pdaodao.springwebplus.task.mapper.ZtTaskNodeMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ZtTaskNodeDao extends BaseDao<ZtTaskNodeMapper, ZtTaskNodeEntity> {
    public void clear(final Date time){
        remove(QueryBuilder.lambda(ZtTaskNodeEntity.class)
                .le(ZtTaskNodeEntity::getUpdateTime, time).build());
    }

    public List<ZtTaskNodeEntity> listEnabled(){
        return list(QueryBuilder.lambda(ZtTaskNodeEntity.class)
                .eq(ZtTaskNodeEntity::getEnabled, true).build());
    }
}