package com.github.pdaodao.springwebplus.ai.core;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.util.ExceptionUtil;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.agentscope.core.model.transport.HttpRequest;
import io.agentscope.core.model.transport.HttpResponse;
import io.agentscope.core.model.transport.HttpTransport;
import io.agentscope.core.model.transport.HttpTransportFactory;
import io.agentscope.core.util.JsonException;
import io.agentscope.core.util.JsonUtils;
import java.util.*;

public class OpenAiEmbeddingModel implements EmbeddingModel {
    private final String baseUrl;
    private final String apiKey;
    private final String model;
    private final int dimension;
    private final HttpTransport httpTransport;

    public OpenAiEmbeddingModel(String baseUrl, String apiKey, String model, int dimension) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.dimension = dimension;
        this.httpTransport = HttpTransportFactory.getDefault();
    }

    public OpenAiEmbeddingModel(String baseUrl, String apiKey, String model) {
        this(baseUrl, apiKey, model, 1024);
    }

    @Override
    public float[] embed(String text) {
        Preconditions.checkNotNull(text, "Text must not be null");
        return embed(Collections.singletonList(text)).get(0);
    }

    @Override
    public List<float[]> embed(final List<String> texts) {
        Preconditions.checkNotNull(texts, "Texts must not be null");
        if (texts.isEmpty()) {
            return Collections.emptyList();
        }
        final String urlStr = buildUrl();
        final HttpRequest.Builder builder = HttpRequest.builder()
                .url(urlStr)
                .method("POST");
        builder.header("Content-Type", "application/json");
        if(StrUtil.isNotBlank(apiKey)){
            builder.header("Authorization",  "Bearer " + apiKey);
        }
        final Map<String, Object> map = new HashMap<>();
        map.put("model",model);
        map.put("dimensions", dimension);
        map.put("input", texts);
        builder.body(JsonUtil.toJsonString(map));

        final HttpResponse httpResponse = httpTransport.execute(builder.build());

        if (!httpResponse.isSuccessful()) {
            int statusCode = httpResponse.getStatusCode();
            String responseBody = httpResponse.getBody();
            String errorMessage =
                    "Embedding API request failed with status "
                            + statusCode
                            + " | "
                            + responseBody;
            throw new RuntimeException(errorMessage, null);
        }

        final String responseBody = httpResponse.getBody();
        final List<float[]> retList = new ArrayList<>(texts.size());
        try {
            final OpenAiEmbeddingResponse ret = JsonUtils.getJsonCodec().fromJson(responseBody, OpenAiEmbeddingResponse.class);
            if(ret.getData() != null){
                for(final OpenAiEmbeddingResponse.EmbeddingData d : ret.getData()){
                    retList.add(d.getEmbedding());
                }
            }
        } catch (JsonException e) {
            throw new RuntimeException(ExceptionUtil.getSimpleMsg(e), e);
        }
        return retList;
    }

    private String buildUrl() {
        String url = baseUrl;
        if (!url.endsWith("/")) {
            url += "/";
        }
        if(StrUtil.contains(url, "v1")){
            return url + "embeddings";
        }
        return url + "v1/embeddings";
    }
}