package com.github.pdaodao.springwebplus.tool.elasticsearch;

import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
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
    private static Map<String, ElasticsearchClient> clientMap = new ConcurrentHashMap<>();

    /**
     * 获取客户端
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static ElasticsearchClient getClient(final String url, final String username, final String password){
        Preconditions.checkNotBlank(url, "elasticsearch connect url is blank.");
        final String key = url;
        ElasticsearchClient t = clientMap.get(key);
        if(t != null){
            return t;
        }
        synchronized (EsUtil.class){
            t = clientMap.get(key);
            if(t != null){
                return t;
            }
            final RestClientBuilder restClientBuilder = RestClient.builder(HttpHost.create(url));
            // 设置超时时间
            restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                    .setConnectTimeout(5000) // 连接超时时间（毫秒）
                    .setSocketTimeout(90000) // 套接字超时时间（毫秒）
            );
            if (StrUtil.isNotBlank(username) && StrUtil.isNotBlank(password)) {
                CredentialsProvider provider = new BasicCredentialsProvider();
                provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(provider));
            }else if(StrUtil.isNotBlank(username)){
                restClientBuilder.setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "Apikey " + username)
                });
            }
            ElasticsearchTransport transport = new RestClientTransport(restClientBuilder.build(), new JacksonJsonpMapper());
            t = new ElasticsearchClient(transport);
            clientMap.put(key, t);
        }
        return t;
    }
}