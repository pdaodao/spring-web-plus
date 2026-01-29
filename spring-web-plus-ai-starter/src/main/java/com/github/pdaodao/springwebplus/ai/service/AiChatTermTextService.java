package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.ListUtil;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.ai.dao.AiChatTermTextDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatTermText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import com.github.pdaodao.springwebplus.ai.store.AiStoreEmbeddingUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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

    public void save(AiChatTermText text) throws Exception{
        termTextDao.save(text);
        if(vectorStore.isEmpty()){
            return;
        }
        final AiEmbedText embedText = new AiEmbedText();
        embedText.setNamespace(AiChatNamespace.term.name());
        embedText.setType("text");
        embedText.setId(text.getId());
        embedText.setTeamId(text.getTeamId());
        embedText.setName(text.getTitle());
        embedText.setTitle(text.getRemark());
        embedText.setContent(text.toEmbeddingText());
        AiStoreEmbeddingUtil.buildForSave(embedText, vectorStore.get(), AiChatModelProvider.ofEmbedding(text.getTeamId(), null));
        vectorStore.get().save(ListUtil.of(embedText));
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
