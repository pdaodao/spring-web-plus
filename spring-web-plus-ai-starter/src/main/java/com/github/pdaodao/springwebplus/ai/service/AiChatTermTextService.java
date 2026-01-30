package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.ListUtil;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.ai.dao.AiChatTermTextDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatTermText;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import com.github.pdaodao.springwebplus.ai.store.AiStoreEmbeddingUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AiChatTermTextService {
    private final AiChatTermTextDao termTextDao;
    private final Optional<AiVectorStore> vectorStore;

    public Boolean checkDistinctTitle(final String teamId, final String id, final String title) {
        return termTextDao.checkDistinctTitle(teamId, id, title);
    }

    public List<AiChatTermText> infoList(final String teamId, final String q){
        return termTextDao.infoList(teamId, q);
    }

    /**
     * 通过关键词到向量库中检索
     */
    public List<AiChatTermText> search(final String teamId, final String q) throws Exception{
        if(vectorStore.isEmpty()){
            return termTextDao.infoList(teamId, q);
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.addNamespace(AiChatNamespace.term.name());
        query.setTeamId(teamId);
        final AiChatContext context = AiChatContext.fromHolder();
        if(context != null && context.getChatApp() != null){
            query.setTopK(context.getChatApp().getAppConfig().getTopK());
            query.setScore(context.getChatApp().getAppConfig().getScore());
        }
        AiStoreEmbeddingUtil.buildQuery(query, AiChatModelProvider.ofEmbedding(teamId, null));
        final List<AiChatTermText> ret = new ArrayList<>();
        final List<AiEmbedText> list = vectorStore.get().query(query);
        for(final AiEmbedText h: list){
            final AiChatTermText term = new AiChatTermText();
            term.setId(h.getId());
            term.setTeamId(h.getTeamId());
            term.setTitle(h.getName());
            term.setRemark(h.getContent());
            ret.add(term);
        }
        return ret;
    }

    public void save(AiChatTermText text) throws Exception{
        termTextDao.save(text);
        if(vectorStore.isEmpty()){
            return;
        }
        final AiEmbedText embedText = new AiEmbedText();
        embedText.setNamespace(AiChatNamespace.term.name());
        embedText.setTeamId(text.getTeamId());
        embedText.setType("text");
        embedText.setId(text.getId());
        embedText.setName(text.getTitle());
        embedText.setTitle(text.getRemark());
        embedText.setContent(text.toEmbeddingText());
        AiStoreEmbeddingUtil.buildForSave(embedText, vectorStore.get(), AiChatModelProvider.ofEmbedding(text.getTeamId(), null));
        vectorStore.get().save(ListUtil.of(embedText), true);
    }

    public Boolean deleteById(final String id) throws Exception{
        Preconditions.checkNotBlank(id, "id不能为空");
        termTextDao.removeById(id);
        if(vectorStore.isEmpty()){
            return true;
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.addNamespace(AiChatNamespace.term.name());
        query.addId(id);
        vectorStore.get().deleteByQuery(query);
        return true;
    }

    public AiChatTermText info(String id) {
        return termTextDao.getById(id);
    }
}
