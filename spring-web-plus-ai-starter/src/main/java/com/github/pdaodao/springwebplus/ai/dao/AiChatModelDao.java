package com.github.pdaodao.springwebplus.ai.dao;

import cn.hutool.core.util.StrUtil;
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

    @Cacheable
    public List<AiChatModel> list(final ChatModelType type,
                                  final String teamId, final Boolean enabled) {
        return list(QueryBuilder.lambda(AiChatModel.class)
                .eq(AiChatModel::getTeamId, teamId)
                .eq(AiChatModel::getEnabled, enabled)
                .eq(AiChatModel::getType, type).build());
    }

    public long countByProvider(final String provider){
        if(StrUtil.isBlank(provider)){
            return 0;
        }
        return count(Wrappers.lambdaQuery(AiChatModel.class)
                .eq(AiChatModel::getProviderId, provider));
    }

    @Cacheable(key = "#p0")
    public AiChatModel detail(final String id) {
        return getById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean save(final AiChatModel entity) {
        return super.save(entity);
    }

    @CacheEvict(allEntries = true)
    public Boolean setEnabled(final String id, final Boolean enabled){
        return update(Wrappers.lambdaUpdate(AiChatModel.class)
                .eq(AiChatModel::getId, id)
                .set(AiChatModel::getEnabled, enabled));
    }
}
