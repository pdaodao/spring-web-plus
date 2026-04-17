package com.github.pdaodao.springwebplus.ai.store.lucene;

import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LuceneTest {
    public static void main(String[] args) throws Exception{
        LuceneStore luceneStore = new LuceneStore(Path.of("/Users/peng/Workspace/work2024/spring-web-plus/spring-web-plus-ai/src/main/resources/lucene"));
        final List<AiEmbedText> list = new ArrayList<>();
        final AiEmbedText t1 = AiEmbedText.of("美国留给伊拉克的是个烂摊子吗");
        t1.setNamespace("test");
        t1.setId("1");
        list.add(t1);

        final AiEmbedText t2 = AiEmbedText.of("公安部：各地校车将享最高路权");
        t2.setNamespace("test");
        t2.setId("2");
        list.add(t2);

        luceneStore.save(list, false);
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        query.addNamespace("test");
        query.setContent("校车");
        query.setScore(0.3);
        final List<AiEmbedText> ret = luceneStore.query(query);
        for(final AiEmbedText text: ret){
            System.out.println(text.getId()+":"+text.getNamespace()+":"+text.getContent());
        }
    }
}
