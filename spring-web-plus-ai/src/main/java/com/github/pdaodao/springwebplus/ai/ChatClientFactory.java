package com.github.pdaodao.springwebplus.ai;

import com.github.pdaodao.springwebplus.ai.client.OpenAiClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatClientFactory {
    final static Map<String, ChatClient> clientMap = new ConcurrentHashMap<>();

    public static ChatClient of(final String provider, final String url, final String key){
        final String id = url+key;
        ChatClient client  = clientMap.get(id);
        if(client != null){
            return client;
        }
        synchronized (ChatClientFactory.clientMap){
            client = clientMap.get(id);
            if(client != null){
                return client;
            }
            client = new OpenAiClient(url, key);
            clientMap.put(id, client);
        }
        return client;
    }
}
