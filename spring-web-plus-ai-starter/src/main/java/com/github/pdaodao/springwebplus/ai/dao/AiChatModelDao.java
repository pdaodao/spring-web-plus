package com.github.pdaodao.springwebplus.ai.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pdaodao.springwebplus.ai.base.ChatModelType;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatModelMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@CacheConfig(cacheNames = "AiChatModel")
public class AiChatModelDao extends BaseDao<AiChatModelMapper, AiChatModel> {

    public List<AiChatModel> list(final ChatModelType type, final String teamId) {
        return list(QueryBuilder.lambda(AiChatModel.class)
                .eq(AiChatModel::getTeamId, teamId)
                .eq(AiChatModel::getType, type).build());
    }

    @Cacheable(key = "#p0")
    public AiChatModel detail(final String id) {
        return getById(id);
    }

    @Override
    @CacheEvict(key = "#p0.id", condition = "#p0.id != null")
    public boolean save(final AiChatModel entity) {
        return super.save(entity);
    }

    @CacheEvict(key = "#p0", condition = "#p0 != null")
    public Boolean setEnabled(final String id, final Boolean enabled){
        return update(Wrappers.lambdaUpdate(AiChatModel.class)
                .eq(AiChatModel::getId, id)
                .set(AiChatModel::getEnabled, enabled));
    }
}
