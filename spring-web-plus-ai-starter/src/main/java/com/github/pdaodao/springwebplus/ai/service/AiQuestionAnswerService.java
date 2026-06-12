package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.ai.dao.AiQuestionAnswerDao;
import com.github.pdaodao.springwebplus.ai.entity.AiQuestionAnswer;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatContext;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import com.github.pdaodao.springwebplus.ai.store.AiStoreEmbeddingUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AiQuestionAnswerService {
    private final AiQuestionAnswerDao dao;
    private final Optional<AiVectorStore> vectorStore;


    public Boolean checkDistinctTitle(final String teamId, final String id, final String question) {
        return dao.checkDistinctTitle(teamId, id, question);
    }

    public List<AiQuestionAnswer> infoList(final String teamId, final String q){
        return dao.infoList(teamId, q);
    }

    public String searchMsg(final String teamId, final String q){
        try{
            final List<AiQuestionAnswer> list = search(teamId, q);
            if(CollUtil.isEmpty(list)){
                return StrUtil.EMPTY;
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("已知以下业务知识:");
            for(final AiQuestionAnswer t: list){
                sb.append("\n -").append(t.getRemark());
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
    public List<AiQuestionAnswer> search(final String teamId, final String q) throws Exception{
        if(vectorStore.isEmpty()){
            return dao.infoList(teamId, q);
        }
        final AiEmbedTextQuery query = AiChatContext.ofQuery();
        query.addNamespace(AiChatNamespace.qa.name());
        query.setTeamId(teamId);
        query.setContent(q);

        AiStoreEmbeddingUtil.buildQuery(query, AiChatModelProvider.ofEmbedding(teamId, null));
        final List<AiQuestionAnswer> ret = new ArrayList<>();
        final List<AiEmbedText> list = vectorStore.get().query(query);
        for(final AiEmbedText h: list){
            final AiQuestionAnswer term = new AiQuestionAnswer();
            term.setId(h.getId());
            term.setTeamId(h.getTeamId());
            term.setTitle(h.getTitle());
            ret.add(term);
        }
        return ret;
    }

    public void save(AiQuestionAnswer text) throws Exception{
        dao.save(text);
        if(vectorStore.isEmpty() || BooleanUtil.isTrue(text.getIsDir())){
            return;
        }
        if(BooleanUtil.isTrue(text.getIsDeleted()) || BooleanUtil.isFalse(text.getEnabled())){
            deleteByIdVector(text.getId());
            return;
        }
        final AiEmbedText embedText = new AiEmbedText();
        embedText.setNamespace(AiChatNamespace.qa.name());
        embedText.setTeamId(text.getTeamId());
        embedText.setType("text");
        embedText.setId(text.getId());
        embedText.setTitle(text.getTitle());
        embedText.setContent(text.toEmbeddingText());
        AiStoreEmbeddingUtil.buildForSave(embedText, vectorStore.get(), AiChatModelProvider.ofEmbedding(text.getTeamId(), null));
        vectorStore.get().save(ListUtil.of(embedText), true);
    }

    public Boolean deleteById(final String id) throws Exception{
        Preconditions.checkNotBlank(id, "id不能为空");
        dao.removeById(id);
        deleteByIdVector(id);
        return true;
    }

    private void deleteByIdVector(final String id) throws Exception{
        if(vectorStore.isEmpty()){
            return;
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.addNamespace(AiChatNamespace.qa.name());
        query.addId(id);
        vectorStore.get().deleteByQuery(query);
    }

    public AiQuestionAnswer info(String id) {
        return dao.getById(id);
    }
}