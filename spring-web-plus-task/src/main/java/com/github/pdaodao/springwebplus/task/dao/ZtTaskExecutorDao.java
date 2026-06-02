package com.github.pdaodao.springwebplus.task.dao;

import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskExecutorEntity;
import com.github.pdaodao.springwebplus.task.mapper.ZtTaskExecutorMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ZtTaskExecutorDao extends BaseDao<ZtTaskExecutorMapper, ZtTaskExecutorEntity> {
    public void clear(final LocalDateTime time){
        remove(QueryBuilder.lambda(ZtTaskExecutorEntity.class)
                .le(ZtTaskExecutorEntity::getUpdateTime, time).build());
    }

    public List<ZtTaskExecutorEntity> listEnabled(){
        return list(QueryBuilder.lambda(ZtTaskExecutorEntity.class)
                .eq(ZtTaskExecutorEntity::getEnabled, true).build());
    }
}