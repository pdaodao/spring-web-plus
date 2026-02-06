package com.github.pdaodao.springwebplus.ai.dao;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatAppMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = "chatApp")
public class AiChatAppDao extends BaseDao<AiChatAppMapper, AiChatApp> {
    @Autowired
    private AiChatModelDao modelDao;

    @Cacheable(key = "#p0")
    public AiChatApp info(final String id) {
        final AiChatApp app = getById(id);
        if (app == null) {
            return null;
        }
        if (StrUtil.isNotBlank(app.getModelId())) {
            final AiChatModel m = modelDao.getById(app.getModelId());
            app.setModelTitle(m.getTitle());
        }
        return app;
    }

    @Override
    @CacheEvict(key = "#p0.id", condition = "#p0.id != null")
    public boolean save(AiChatApp entity) {
        return super.save(entity);
    }
}