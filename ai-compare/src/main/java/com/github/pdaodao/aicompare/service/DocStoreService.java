package com.github.pdaodao.aicompare.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.aicompare.core.DocText;
import com.github.pdaodao.aicompare.store.DocEmbeddingUtil;
import com.github.pdaodao.aicompare.store.DocVectorStore;
import com.github.pdaodao.aicompare.core.DocTextQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DocStoreService {
    private final DocVectorStore store;

    public void saveList(final List<DocText> list) throws Exception{
        if(CollUtil.isEmpty(list)){
            return;
        }
        for(final DocText d: list){
            if(StrUtil.isBlank(d.getNamespace())){
                d.setNamespace("doc");
            }
            if(d.getEmbedding() == null){
                d.setEmbedding(DocEmbeddingUtil.embedding(d.getContent()));
            }
        }
        store.save(list);
    }

    public List<DocText> query(final DocTextQuery query) throws Exception{
        return store.query(query);
    }

    public long count() throws Exception{
        return store.count();
    }
}
