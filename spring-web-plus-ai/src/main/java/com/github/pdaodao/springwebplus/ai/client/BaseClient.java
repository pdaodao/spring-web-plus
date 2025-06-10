package com.github.pdaodao.springwebplus.ai.client;

import com.github.pdaodao.springwebplus.ai.support.ChatResponseMsgListener;
import com.github.pdaodao.springwebplus.ai.support.ChatRole;
import com.github.pdaodao.springwebplus.ai.support.LLMChatRequest;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import lombok.Data;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Data
public class BaseClient implements AutoCloseable {
    protected final String sseUrl;
    protected final String key;
    protected OkHttpClient httpClient;

    public BaseClient(String sseUrl, String key) {
        this.sseUrl = sseUrl;
        this.key = key;
    }

    protected OkHttpClient getHttpClient(){
        if(httpClient != null){
            return httpClient;
        }
        synchronized (this){
            if(httpClient != null){
                return httpClient;
            }
            final OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            httpClient = client;
        }
        return httpClient;
    }
    protected void tryStream(final LLMChatRequest msg, final ChatResponseMsgListener listener) throws IOException{
        tryStream(JsonUtil.toJsonString(msg), listener);
    }

    protected void tryStream(final String msg, final ChatResponseMsgListener msgListener) throws IOException {
        final Request request = new Request.Builder()
                .url(sseUrl)
                .header("Accept", "text/event-stream")
                .header("Authorization", "Bearer "+key)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), msg))
                .build();
        final OkHttpClient client = getHttpClient();
        final EventSource eventSource = EventSources.createFactory(client).newEventSource(request, eventSourceListener(msgListener));
    }

    protected EventSourceListener eventSourceListener(final ChatResponseMsgListener msgListener){
        return new OpenAiListener(msgListener);
    }


    @Override
    public void close() throws Exception {

    }

    public static void main(String[] args) throws Exception{
        BaseClient baseClient = new BaseClient("https://api.moonshot.cn/v1/chat/completions", "sk-ZLa0BR60syCiY8qD6zsCBUlFMQqXgwWnwlGiCt2G7jAUrmrW");

        final LLMChatRequest req = new LLMChatRequest();
        req.model("moonshot-v1-8k");
        req.addMsg(ChatRole.user, "你是谁");
        req.setStream(true);

        baseClient.tryStream(req, new ChatResponseMsgListener());
        System.out.println("end...");
    }
}