package com.github.pdaodao.aicompare.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.github.pdaodao.aicompare.store.DocVectorStore;
import com.github.pdaodao.aicompare.store.elasticsearch.EsDocVectorStore;
import com.github.pdaodao.aicompare.store.elasticsearch.EsUtil;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "ai.vector.type", havingValue = "es")
public class EsStoreAutoConfig {
    @Value("${ai.vector.host}")
    public String host;

    @Value("${ai.vector.username}")
    public String username;

    @Value("${ai.vector.password}")
    public String password;

    @Bean
    public DocVectorStore vectorStore() throws Exception{
        final DbInfo dbInfo = new DbInfo();
        dbInfo.setUrl(host);
        dbInfo.setUsername(username);
        dbInfo.setPassword(password);
        final ElasticsearchClient client = EsUtil.getClient(dbInfo);
        final EsDocVectorStore store = new EsDocVectorStore(client);
        store.init();
        return store;
    }
}
