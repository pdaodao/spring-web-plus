package com.github.pdaodao.springwebplus.ai.dao;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.ai.entity.AiChatAppTopic;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatAppTopicMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AiChatAppTopicDao extends BaseDao<AiChatAppTopicMapper, AiChatAppTopic> {
    public List<AiChatAppTopic> byAppId(final String appId){
        return list(QueryBuilder.lambda(AiChatAppTopic.class)
                .eq(AiChatAppTopic::getAppId, appId)
                .build());
    }

    public Boolean saveTopics(final String appId, final List<AiChatAppTopic> list){
        if(CollUtil.isNotEmpty(list)){
            for(AiChatAppTopic t: list){
                t.setAppId(appId);
            }
        }
        final List<AiChatAppTopic> old = byAppId(appId);
        return saveDiffListById(list, old);
    }
}
