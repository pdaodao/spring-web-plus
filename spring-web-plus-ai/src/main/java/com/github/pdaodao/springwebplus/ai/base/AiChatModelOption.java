package com.github.pdaodao.springwebplus.ai.base;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AiChatModelOption {
    @Schema(description = "基础地址")
    private String baseUrl;

    @Schema(description = "请求key")
    private String apiKey;

    @Schema(description = "模型")
    private String model;

    @Schema(description = "采样温度")
    private Double temperature;

    public static AiChatModelOption of(final String baseUrl, final String apiKey, final String model){
        final AiChatModelOption option = new AiChatModelOption();
        option.setBaseUrl(baseUrl);
        option.setApiKey(apiKey);
        option.setModel(model);
        return option;
    }

    public String key(){
        return StrUtil.join(",", baseUrl, apiKey, model);
    }
}
