package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.BooleanUtil;
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

import java.util.List;
import java.util.Optional;

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
        if (ChatDocNamespace.doc == doc.getDocNamespace()) {
            doc.setItemCount(itemDao.countByDocId(id));
            return doc;
        }
        doc.setDocItems(itemDao.listByDocId(id));
        return doc;
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
        if(!aiVectorStoreOptional.isPresent()){
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
        itemDao.save(item);
        if(!aiVectorStoreOptional.isPresent()) {
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
        embedText.setTeamId(doc.getTeamId());
        embedText.setTopic(doc.getPid());

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
        if(!aiVectorStoreOptional.isPresent()){
            return true;
        }
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.setTextIds(ListUtil.of(id));
        aiVectorStoreOptional.get().deleteByQuery(query);
        return true;
    }
}
