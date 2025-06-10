package com.github.pdaodao.springwebplus.ai;

import com.github.pdaodao.springwebplus.ai.pojo.DocText;
import com.github.pdaodao.springwebplus.ai.pojo.DocTextQuery;

import java.util.List;

public interface DocVectorStore {
    void init() throws Exception;

    void save(final List<DocText> docTextList) throws Exception;

    void delete(final List<String> ids) throws Exception;


    List<DocText> query(final DocTextQuery docText) throws Exception;
}