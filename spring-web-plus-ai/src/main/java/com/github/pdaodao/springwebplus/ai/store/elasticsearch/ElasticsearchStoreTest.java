package com.github.pdaodao.springwebplus.ai.store.elasticsearch;

import cn.hutool.core.util.IdUtil;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ElasticsearchStoreTest {

    public static void main1(String[] args) throws Exception {
        final ElasticsearchOptions opt = ElasticsearchOptions.of("http://127.0.0.1:9200", "ai_test1");
        final ElasticsearchVectorStore store = new ElasticsearchVectorStore(opt, 1024);

        final List<AiEmbedText> list = new ArrayList<>();
        final AiEmbedText t1 = AiEmbedText.of("美国留给伊拉克的是个烂摊子吗");
        t1.setNamespace("test");
        t1.setId(IdUtil.fastUUID());
        list.add(t1);

        final AiEmbedText t2 = AiEmbedText.of("公安部：各地校车将享最高路权");
        t2.setNamespace("test");
        t2.setId(IdUtil.fastUUID());
        list.add(t2);

        final AiEmbedText t3 = AiEmbedText.of("中韩渔警冲突调查：韩警平均每天扣1艘中国渔船");
        t3.setNamespace("test");
        t3.setId(IdUtil.fastUUID());
        list.add(t3);

        final AiEmbedText t4 = AiEmbedText.of("中国驻洛杉矶领事馆遭亚裔男子枪击 嫌犯已自首2");
        t4.setNamespace("test");
        t4.setId("db1d3ff1-ba90-44af-a4bf-40012e9ebecc");
        list.add(t4);
        store.save(list, true);

        System.out.println("hello");
    }

    public static void main(String[] args) throws Exception {
        final ElasticsearchOptions opt = ElasticsearchOptions.of("http://127.0.0.1:9200", "ai_test1");
        final ElasticsearchVectorStore store = new ElasticsearchVectorStore(opt, 1024);
        final AiEmbedTextQuery q = AiEmbedTextQuery.of("中国渔船冲突");
        q.setScore(0.7);
        final List<AiEmbedText> ret = store.query(q);
        System.out.println("hello");
    }
}
