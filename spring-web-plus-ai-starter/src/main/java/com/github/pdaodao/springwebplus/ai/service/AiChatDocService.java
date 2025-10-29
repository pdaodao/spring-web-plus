package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.dao.AiChatDocDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatDocItemDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatDoc;
import com.github.pdaodao.springwebplus.ai.entity.AiChatDocItem;
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
public class AiChatDocService {
    private final AiChatDocDao docDao;
    private final AiChatDocItemDao itemDao;
    private final Optional<AiVectorStore> aiVectorStoreOptional;

    public List<AiChatDoc> list(final AiChatDocQuery query) {
        return docDao.list(query);
    }

    public AiChatDoc info(final String id) {
        final AiChatDoc doc = docDao.getById(id);
        if (doc == null) {
            return null;
        }
        if (ChatDocNamespace.file == doc.getNamespace()) {
            doc.setItemCount(itemDao.countByDocId(id));
            return doc;
        }
        doc.setDocItems(itemDao.listByDocId(id));
        return doc;
    }

    public List<AiEmbedText> search(final String key, final Double score) throws Exception {
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.setContent(key);
        query.setScore(score);
        return aiVectorStoreOptional.get().query(query);
    }

    public void saveInfo(final AiChatDoc aiChatDoc) throws Exception {
        boolean next = docDao.saveRich(aiChatDoc);
        if(BooleanUtil.isFalse(next) || !aiVectorStoreOptional.isPresent()){
            return;
        }
        final String namespace = aiChatDoc.getNamespace();
        final String type = aiChatDoc.itemType();

        final AiEmbedText embedText = new AiEmbedText();
        embedText.setNamespace(namespace);
        embedText.setType(namespace);
        embedText.setId(aiChatDoc.getId());
        embedText.setTopic(aiChatDoc.getPid());
        embedText.setDocId(aiChatDoc.getId());
        embedText.setTextId("0");
        embedText.setName(aiChatDoc.getName());
        embedText.setTitle(aiChatDoc.getTitle());
        embedText.setTeamId(aiChatDoc.getTeamId());
        embedText.setContent(aiChatDoc.content());

        final List<AiEmbedText> toEmbedList = new ArrayList<>();
        toEmbedList.add(embedText);

        for (final AiChatDocItem item : itemDao.listByDocId(aiChatDoc.getId())) {
            final AiEmbedText itemText = itemToAiEmbedText(item, aiChatDoc);
            embedText.setNamespace(namespace);
            embedText.setType(type);
            toEmbedList.add(itemText);
        }
        if(CollUtil.isEmpty(toEmbedList)){
            return;
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.setDocIds(ListUtil.of(aiChatDoc.getId()));
        final List<AiEmbedText>  oldList = aiVectorStoreOptional.get().query(query);
        final Map<String, float[]> oldMap = new LinkedHashMap<>();
        for(final AiEmbedText t: oldList){
            if(StrUtil.isNotBlank(t.getContent()) && t.getEmbedding() != null){
                oldMap.put(t.getContent(), t.getEmbedding());
            }
        }
        for(final AiEmbedText t: toEmbedList){
            final float[] ff = oldMap.get(t.getContent());
            if(ArrayUtil.isNotEmpty(ff)){
                t.setEmbedding(ff);
            }
        }
        aiVectorStoreOptional.get().deleteByQuery(query);
        aiVectorStoreOptional.get().add(toEmbedList);
    }

    public Boolean delete(final String id) throws Exception {
        final AiChatDoc doc = docDao.getById(id);
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
        query.setDocIds(ListUtil.of(id));
        aiVectorStoreOptional.get().deleteByQuery(query);
        return true;
    }

    /**
     * 保存文档的文本块
     *
     * @param item
     * @return
     */
    public AiChatDocItem saveDocItem(final AiChatDocItem item) throws Exception {
        Preconditions.checkNotBlank(item.getDocId(), "文档id不能为空.");
        final AiChatDoc doc = docDao.getById(item.getDocId());
        Preconditions.checkNotBlank(item.getDocId(), "文档不存在.");
        final String type = doc.itemType();
        item.setType(type);
        itemDao.save(item);
        if (!aiVectorStoreOptional.isPresent()) {
            return item;
        }
        // 向量化
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.setTextIds(ListUtil.of(item.getId()));
        aiVectorStoreOptional.get().deleteByQuery(query);

        final AiEmbedText embedText = itemToAiEmbedText(item, doc);
        aiVectorStoreOptional.get().add(ListUtil.of(embedText));
        return item;
    }

    public static AiEmbedText itemToAiEmbedText(final AiChatDocItem item, final AiChatDoc doc) {
        final AiEmbedText embedText = new AiEmbedText();
        embedText.setNamespace(doc.getNamespace());
        embedText.setTeamId(doc.getTeamId());
        embedText.setTopic(doc.getPid());

        embedText.setType(item.getType());
        embedText.setId(item.getId());
        embedText.setTextId(item.getId());
        embedText.setDocId(item.getDocId());
        embedText.setName(item.getName());
        embedText.setTitle(item.getTitle());
        embedText.setContent(item.getContent());
        return embedText;
    }

    /**
     * 删除文档的文本块
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Boolean deleteDocItem(final String id) throws Exception {
        final AiChatDocItem docText = itemDao.getById(id);
        if (docText == null) {
            return false;
        }
        itemDao.removeById(id);
        if (!aiVectorStoreOptional.isPresent()) {
            return true;
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.setTextIds(ListUtil.of(id));
        aiVectorStoreOptional.get().deleteByQuery(query);
        return true;
    }
}
