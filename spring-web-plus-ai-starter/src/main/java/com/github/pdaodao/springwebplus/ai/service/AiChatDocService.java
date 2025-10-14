package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.dao.AiChatDocDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatDocItemDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatDoc;
import com.github.pdaodao.springwebplus.ai.pojo.ChatDocNamespace;
import com.github.pdaodao.springwebplus.ai.query.AiChatDocQuery;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@AllArgsConstructor
public class AiChatDocService {
    private final AiChatDocDao docDao;
    private final AiChatDocItemDao itemDao;

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

    public Boolean delete(final String id){
        return true;
    }
}
