package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.ai.entity.AiChatText;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import com.github.pdaodao.springwebplus.ai.store.AiStoreEmbeddingUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@AllArgsConstructor
public class AiVectorStoreService {
    private final Optional<AiVectorStore> vectorStore;

    public boolean isEmpty(){
        return vectorStore.isEmpty();
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


    @Async
    public void save(final String teamId, final AiChatText text) throws Exception{
        if(vectorStore.isEmpty() || text == null || StrUtil.isBlank(text.toEmbeddingText())){
            return;
        }
        final AiEmbedText embedText = toAiEmbedText(text);
        AiStoreEmbeddingUtil.buildForSave(embedText, vectorStore.get(), AiChatModelProvider.ofEmbedding(teamId, null));
        vectorStore.get().save(ListUtil.of(embedText), true);
    }

    @Async
    public void saveBatch(final String teamId, final List<AiChatText> list) throws Exception{
        if(vectorStore.isEmpty() || CollUtil.isEmpty(list)){
            return;
        }
        final List<AiEmbedText> textList = new ArrayList<>();
        for(final AiChatText t: list){
            textList.add(toAiEmbedText(t));
        }
        AiStoreEmbeddingUtil.buildBatch(textList, AiChatModelProvider.ofEmbedding(teamId, null));
        vectorStore.get().save(textList, false);
    }

    @Async
    public void deleteById(final String id, final String namespace) throws Exception{
        Preconditions.checkNotBlank(id, "id不能为空");
        if(vectorStore.isEmpty()){
            return;
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.addId(id);
        query.addNamespace(namespace);
        vectorStore.get().deleteByQuery(query);
    }


    public List<AiChatText> search(final String teamId, final String q) throws Exception {
        if(vectorStore.isEmpty()){
            return null;
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

}