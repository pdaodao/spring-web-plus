package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.ai.dao.AiChatTextDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatText;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.query.AiChatTextQuery;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import com.github.pdaodao.springwebplus.ai.store.AiStoreEmbeddingUtil;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class AiChatTextService {
    private final AiChatTextDao termTextDao;
    private final Optional<AiVectorStore> vectorStore;

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
            final StringBuilder sb = new StringBuilder();
            sb.append("已知以下业务知识:");
            for(final AiChatText t: list){
                sb.append("\n -").append(t.getContent());
            }
            log.info("业务术语检索:"+q);
            log.info(sb.toString());
            return sb.toString();
        }catch (final Exception e){
            log.error(e.getMessage(), e);
        }
        return StrUtil.EMPTY;
    }


    /**
     * 通过关键词到向量库中检索
     */
    public List<AiChatText> search(final String teamId, final String q) throws Exception{
        if(vectorStore.isEmpty()){
            final AiChatTextQuery query = new AiChatTextQuery();
            query.setTeamId(teamId);
            query.setQ(q);
            return termTextDao.infoList(query);
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        // 先在所有里面搜 后续再可配置化
        // query.addNamespace(AiChatNamespace.term.name());
        query.setTeamId(teamId);
        query.setContent(q);
        final AiChatContext context = AiChatContext.fromHolder();
        if(context != null && context.getChatApp() != null && context.getChatApp().getAppConfig() != null){
            query.setTopK(context.getChatApp().getAppConfig().getTopK());
            query.setScore(context.getChatApp().getAppConfig().getScore());
        }else{
            query.setTopK(5);
            query.setScore(0.6);
        }
        AiStoreEmbeddingUtil.buildQuery(query, AiChatModelProvider.ofEmbedding(teamId, null));
        final List<AiChatText> ret = new ArrayList<>();
        final List<AiEmbedText> list = vectorStore.get().query(query);
        for(final AiEmbedText h: list){
            final AiChatText term = new AiChatText();
            term.setId(h.getId());
            term.setNamespace(AiChatNamespace.valueOf(h.getNamespace()));
            term.setTopicId(h.getTopic());
            term.setFileId(h.getDocId());
            term.setTeamId(h.getTeamId());
            term.setTitle(h.getTitle());
            term.setContent(h.getContent());
            ret.add(term);
        }
        return ret;
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
        final List<AiEmbedText> toAddList = new ArrayList<>();
        for(final AiChatText t: list){
            if(StrUtil.isNotBlank(t.getTitle()) && old.contains(t.getTitle())){
                continue;
            }
            if(old.contains(t.toEmbeddingText())){
                continue;
            }
            termTextDao.save(t);
            toAddList.add(toAiEmbedText(t));
        }
        if(vectorStore.isEmpty()){
            return;
        }
        AiStoreEmbeddingUtil.buildBatch(toAddList, AiChatModelProvider.ofEmbedding(teamId, null));
        vectorStore.get().save(toAddList, false);
    }

    public void save(final AiChatText text) throws Exception{
        termTextDao.save(text);
        if(vectorStore.isEmpty()){
            return;
        }
        final AiEmbedText embedText = toAiEmbedText(text);
        AiStoreEmbeddingUtil.buildForSave(embedText, vectorStore.get(), AiChatModelProvider.ofEmbedding(text.getTeamId(), null));
        vectorStore.get().save(ListUtil.of(embedText), true);
    }

    private static AiEmbedText toAiEmbedText(final AiChatText text){
        final AiEmbedText embedText = new AiEmbedText();
        embedText.setNamespace(text.getNamespace().name());
        embedText.setTopic(text.getTopicId());
        embedText.setId(text.getId());
        embedText.setDocId(text.getFileId());
        embedText.setTeamId(text.getTeamId());
        embedText.setTitle(text.getTitle());
        embedText.setContent(text.toEmbeddingText());
        return embedText;
    }

    public Boolean deleteById(final String id) throws Exception{
        Preconditions.checkNotBlank(id, "id不能为空");
        termTextDao.removeById(id);
        if(vectorStore.isEmpty()){
            return true;
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.addId(id);
        vectorStore.get().deleteByQuery(query);
        return true;
    }

    public AiChatText info(String id) {
        return termTextDao.getById(id);
    }
}
