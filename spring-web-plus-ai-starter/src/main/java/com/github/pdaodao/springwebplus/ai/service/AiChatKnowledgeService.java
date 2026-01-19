package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.dao.AiChatKnowledgeChunkDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatKnowledgeDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatKnowledgeChunkDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatKnowledge;
import com.github.pdaodao.springwebplus.ai.entity.AiChatKnowledgeChunk;
import com.github.pdaodao.springwebplus.ai.pojo.ChatDocNamespace;
import com.github.pdaodao.springwebplus.ai.query.AiChatDocQuery;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class AiChatKnowledgeService {
    private final AiChatKnowledgeDao docDao;
    private final AiChatKnowledgeChunkDao itemDao;
    private final Optional<AiVectorStore> aiVectorStoreOptional;

    public List<AiChatKnowledge> list(final AiChatDocQuery query) {
        return docDao.list(query);
    }

    public AiChatKnowledge info(final String id) {
        final AiChatKnowledge doc = docDao.getById(id);
        if (doc == null) {
            return null;
        }
        doc.setChunkList(itemDao.listByDocId(id));
        return doc;
    }

    public List<AiEmbedText> search(final String key, final Double score) throws Exception {
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.setContent(key);
        query.setScore(score);
        return aiVectorStoreOptional.get().query(query);
    }

    public void saveInfo(final AiChatKnowledge aiChatDoc) throws Exception {
        boolean next = docDao.saveRich(aiChatDoc);
        if(BooleanUtil.isFalse(next) || !aiVectorStoreOptional.isPresent()){
            return;
        }
//        final String type = aiChatDoc.itemType();
//
//        final AiEmbedText embedText = new AiEmbedText();
//        embedText.setNamespace(namespace);
//        embedText.setType(namespace);
//        embedText.setId(aiChatDoc.getId());
//        embedText.setTopic(aiChatDoc.getPid());
//        embedText.setDocId(aiChatDoc.getId());
//        embedText.setId("0");
//        embedText.setName(aiChatDoc.getName());
//        embedText.setTitle(aiChatDoc.getTitle());
//        embedText.setTeamId(aiChatDoc.getTeamId());
//        embedText.setContent(aiChatDoc.content());
//
//        final List<AiEmbedText> toEmbedList = new ArrayList<>();
//        toEmbedList.add(embedText);
//
//        for (final AiChatKnowledgeChunk item : itemDao.listByDocId(aiChatDoc.getId())) {
//            final AiEmbedText itemText = itemToAiEmbedText(item, aiChatDoc);
//            embedText.setNamespace(namespace);
//            embedText.setType(type);
//            toEmbedList.add(itemText);
//        }
//        if(CollUtil.isEmpty(toEmbedList)){
//            return;
//        }
//        final AiEmbedTextQuery query = new AiEmbedTextQuery();
//        query.addDocId(aiChatDoc.getId());
//        final List<AiEmbedText>  oldList = aiVectorStoreOptional.get().query(query);
//        final Map<String, float[]> oldMap = new LinkedHashMap<>();
//        for(final AiEmbedText t: oldList){
//            if(StrUtil.isNotBlank(t.getContent()) && t.getEmbedding() != null){
//                oldMap.put(t.getContent(), t.getEmbedding());
//            }
//        }
//        for(final AiEmbedText t: toEmbedList){
//            final float[] ff = oldMap.get(t.getContent());
//            if(ArrayUtil.isNotEmpty(ff)){
//                t.setEmbedding(ff);
//            }
//        }
//        aiVectorStoreOptional.get().deleteByQuery(query);
//        aiVectorStoreOptional.get().save(toEmbedList);
    }

    public Boolean delete(final String id) throws Exception {
        final AiChatKnowledge doc = docDao.getById(id);
        if (doc == null) {
            return false;
        }
        docDao.removeById(id);
        if (BooleanUtil.isTrue(doc.getIsDir())) {
            return true;
        }
        if (!aiVectorStoreOptional.isPresent()) {
            return true;
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.addDocId(id);
        aiVectorStoreOptional.get().deleteByQuery(query);
        return true;
    }

    /**
     * 保存文档的文本块
     *
     * @param item
     * @return
     */
    public AiChatKnowledgeChunk saveDocItem(final AiChatKnowledgeChunk item) throws Exception {
//        Preconditions.checkNotBlank(item.getDocId(), "文档id不能为空.");
//        final AiChatKnowledge doc = docDao.getById(item.getDocId());
//        Preconditions.checkNotBlank(item.getDocId(), "文档不存在.");
//        final String type = doc.itemType();
//        item.setType(type);
//        itemDao.save(item);
//        if (!aiVectorStoreOptional.isPresent()) {
//            return item;
//        }
//        // 向量化
//        final AiEmbedTextQuery query = new AiEmbedTextQuery();
//        query.addId(item.getId());
//        aiVectorStoreOptional.get().deleteByQuery(query);
//
//        final AiEmbedText embedText = itemToAiEmbedText(item, doc);
//        aiVectorStoreOptional.get().save(ListUtil.of(embedText));
//        return item;
        return null;
    }

    public static AiEmbedText itemToAiEmbedText(final AiChatKnowledgeChunk item, final AiChatKnowledge doc) {
//        final AiEmbedText embedText = new AiEmbedText();
//        embedText.setNamespace(doc.getNamespace());
//        embedText.setTeamId(doc.getTeamId());
//        embedText.setTopic(doc.getPid());
//
//        embedText.setType(item.getType());
//        embedText.setId(item.getId());
//        embedText.setId(item.getId());
//        embedText.setDocId(item.getDocId());
//        embedText.setName(item.getName());
//        embedText.setTitle(item.getTitle());
//        embedText.setContent(item.getContent());
//        return embedText;
        return null;
    }

    /**
     * 删除文档的文本块
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Boolean deleteDocItem(final String id) throws Exception {
        final AiChatKnowledgeChunk docText = itemDao.getById(id);
        if (docText == null) {
            return false;
        }
        itemDao.removeById(id);
        if (!aiVectorStoreOptional.isPresent()) {
            return true;
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.setIds(Set.of(id));
        aiVectorStoreOptional.get().deleteByQuery(query);
        return true;
    }
}
