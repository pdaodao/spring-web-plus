package com.github.pdaodao.aicompare.store;

import com.github.pdaodao.aicompare.core.DocText;
import com.github.pdaodao.aicompare.core.DocTextQuery;

import java.util.List;

public interface DocVectorStore {
    void init() throws Exception;

    void save(final List<DocText> docTextList) throws Exception;

    void delete(final List<String> ids) throws Exception;


    List<DocText> query(final DocTextQuery docText) throws Exception;

    public long count() throws Exception;
}