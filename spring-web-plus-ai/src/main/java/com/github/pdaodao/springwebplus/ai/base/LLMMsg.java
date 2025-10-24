package com.github.pdaodao.springwebplus.ai.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class LLMMsg {
    @Schema(description = "消息类型标识")
    private LLMMsgType type;

    @Schema(description = "文本内容")
    private String text;

    @Schema(description = "文本消息的引用")
    private List<Annotation> annotations;

    @Schema(description = "图片/文件内容 base64")
    private String base64;

    @Schema(description = "图片请求地址")
    private String url;

    // text/plain, text/markdown
    @Schema(description = "图片类型 image/jpeg, image/png, audio/wav,video/mp4,application/pdf")
    private String mimeType;

    private String query;

    // 文件id, 函数调用id
    private String id;

    private Object extras;

    private String error;

    // 函数名称
    private String name;
    // 函数参数
    private Object args;
    private String index;

    // 非标准
    private Object value;

    @Data
    public static class Annotation{
        private String id;
        private String title;
        private String snippet;
        private String url;
    }
}
