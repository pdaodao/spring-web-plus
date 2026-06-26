package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.dao.AiChatTextDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatText;
import com.github.pdaodao.springwebplus.ai.query.AiChatTextQuery;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class AiChatTextService {
    private final AiChatTextDao termTextDao;
    private final AiVectorStoreService vectorStore;

    public Boolean checkDistinctTitle(final String teamId, final String id, final String title, final String topic) {
        return termTextDao.checkDistinctTitle(teamId, id, title, topic);
    }

    public List<AiChatText> infoList(final AiChatTextQuery query){
        return termTextDao.infoList(query);
    }

    public String searchMsg(final String teamId, final String q){
        try{
            final List<AiChatText> list = search(teamId, q);
            if(CollUtil.isEmpty(list)){
                return StrUtil.EMPTY;
            }
            final String ret = buildMsg(list);
            log.info("业务术语检索:"+q);
            log.info(ret);
            return ret;
        }catch (final Exception e){
            log.error(e.getMessage(), e);
        }
        return StrUtil.EMPTY;
    }

    public static String buildMsg(final List<AiChatText> list){
        if(CollUtil.isEmpty(list)){
            return StrUtil.EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("已知以下业务知识:\n");
        for(final AiChatText t: list){
            sb.append("#").append(t.getContent()).append("\n");
        }
        return sb.toString();
    }


    /**
     * 通过关键词到向量库中检索
     */
    public List<AiChatText> search(final String teamId, final String q) throws Exception {
        if(vectorStore.isEmpty()){
            final AiChatTextQuery query = new AiChatTextQuery();
            query.setTeamId(teamId);
            query.setQ(q);
            return termTextDao.infoList(query);
        }
        return vectorStore.search(teamId, q);
    }

    public void saveBatch(final String topicId, final String teamId, final List<AiChatText> list) throws Exception{
        if(CollUtil.isEmpty(list)){
            return;
        }
        final AiChatTextQuery query = new AiChatTextQuery();
        query.setTopicId(topicId);
        final List<AiChatText> oldList = termTextDao.infoList(query);
        final Set<String> old = new HashSet<>();
        for(final AiChatText t: oldList){
            if(StrUtil.isNotBlank(t.getTitle())){
                old.add(t.getTitle());
            }
            old.add(t.toEmbeddingText());
        }
        final List<AiChatText> toAddList = new ArrayList<>();
        for(final AiChatText t: list){
            if(StrUtil.isNotBlank(t.getTitle()) && old.contains(t.getTitle())){
                continue;
            }
            if(old.contains(t.toEmbeddingText())){
                continue;
            }
            termTextDao.save(t);
            toAddList.add(t);
        }
        vectorStore.saveBatch(teamId, toAddList);
    }

    public void save(final AiChatText text) throws Exception{
        termTextDao.save(text);
        if(vectorStore.isEmpty()){
            return;
        }
        vectorStore.save(text.getTeamId(), text);
    }

    public Boolean deleteById(final String id) throws Exception{
        Preconditions.checkNotBlank(id, "id不能为空");
        termTextDao.removeById(id);
        vectorStore.deleteById(id, null);
        return true;
    }

    public AiChatText info(String id) {
        return termTextDao.getById(id);
    }
}
