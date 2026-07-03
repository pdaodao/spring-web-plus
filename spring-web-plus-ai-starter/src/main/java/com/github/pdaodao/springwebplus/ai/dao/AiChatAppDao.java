package com.github.pdaodao.springwebplus.ai.dao;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.entity.AiChatAppTopic;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatAppMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@CacheConfig(cacheNames = "chatApp")
public class AiChatAppDao extends BaseDao<AiChatAppMapper, AiChatApp> {
    @Autowired
    private AiChatModelDao modelDao;

    @Autowired
    private AiChatAppTopicDao appTopicDao;

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
        final List<AiChatAppTopic> topics = appTopicDao.byAppId(id);
        app.setTopics(topics);
        return app;
    }

    @Override
    @CacheEvict(key = "#p0.id", condition = "#p0.id != null")
    public boolean save(AiChatApp entity) {
        super.save(entity);
        if(entity.getTopics() != null){
            appTopicDao.saveTopics(entity.getId(), entity.getTopics());
        }
        return true;
    }
}