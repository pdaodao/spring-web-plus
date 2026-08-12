package com.github.pdaodao.springwebplus.ai.dao;

import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.ai.entity.AiChatTopic;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatTopicMapper;
import com.github.pdaodao.springwebplus.ai.query.AiChatTextQuery;
import com.github.pdaodao.springwebplus.ai.query.AiChatTopicQuery;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AiChatTopicDao extends BaseDao<AiChatTopicMapper, AiChatTopic> {
    public List<AiChatTopic> infoList(final AiChatTopicQuery query){
        return list(QueryBuilder.lambda(AiChatTopic.class)
                .eq(AiChatTopic::getNamespace, query.getNamespace())
                .eq(AiChatTopic::getPid, query.getPid())
                .build());
    }

    public List<AiChatTopic> fileList(final AiChatTextQuery query){
        return baseMapper.fileList(query);
    }


    public AiChatTopic saveInfo(final AiChatNamespace namespace, final String teamId, final String title, final String id){
        final AiChatTopic t = new AiChatTopic();
        t.setNamespace(namespace);
        t.setTeamId(teamId);
        t.setTitle(title);
        t.setId(id);
        save(t);
        return t;
    }

    public AiChatTopic saveByTitle(final AiChatNamespace namespace, final String teamId, final String title){
        Preconditions.checkNotBlank(title, "title is empty.");
        final AiChatTopic topic = getOne(QueryBuilder.lambda(AiChatTopic.class)
                .eq(AiChatTopic::getNamespace, namespace)
                .eq(AiChatTopic::getTeamId, teamId)
                .eq(AiChatTopic::getTitle, title).build());
        if(topic != null){
            return topic;
        }
        final AiChatTopic t = new AiChatTopic();
        t.setNamespace(namespace);
        t.setTeamId(teamId);
        t.setTitle(title);
        save(t);
        return t;
    }

    public Boolean delete(final String id){
        long subCnt = SpringUtil.getBean(AiChatTextDao.class)
                .countByTopic(id);
        Preconditions.assertTrue(subCnt > 0, "存在子项无法删除.");
        return removeById(id);
    }
}