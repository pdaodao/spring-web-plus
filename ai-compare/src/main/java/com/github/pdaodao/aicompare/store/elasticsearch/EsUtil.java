package com.github.pdaodao.aicompare.store.elasticsearch;

import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EsUtil {
    public static final String ChatIndexName = "ai_doc1";

    private static Map<String, ElasticsearchClient> clientMap = new ConcurrentHashMap<>();

    public static ElasticsearchClient getClient(final DbInfo dbInfo){
        Preconditions.checkNotNull(dbInfo, "Elasticsearch connect info is null.");
        Preconditions.checkNotBlank(dbInfo.getUrl(), "Elasticsearch connect url is null.");
        final String key = dbInfo.key();
        ElasticsearchClient t = clientMap.get(key);
        if(t != null){
            return t;
        }
        synchronized (EsUtil.class){
            t = clientMap.get(key);
            if(t != null){
                return t;
            }
            final RestClientBuilder restClientBuilder = RestClient.builder(HttpHost.create(dbInfo.getUrl()));
            // 设置超时时间
            restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                    .setConnectTimeout(5000) // 连接超时时间（毫秒）
                    .setSocketTimeout(90000) // 套接字超时时间（毫秒）
            );
            if (StrUtil.isNotBlank(dbInfo.getUsername()) && StrUtil.isNotBlank(dbInfo.getPassword())) {
                CredentialsProvider provider = new BasicCredentialsProvider();
                provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(dbInfo.getUsername(), dbInfo.getPassword()));
                restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(provider));
            }else if(StrUtil.isNotBlank(dbInfo.getUsername())){
                restClientBuilder.setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "Apikey " + dbInfo.getUsername())
                });
            }
            ElasticsearchTransport transport = new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper());
            t = new ElasticsearchClient(transport);
            clientMap.put(key, t);
        }
        return t;
    }
}