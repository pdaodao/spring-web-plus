package com.github.pdaodao.springwebplus.ai.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatAsk {
    private String appId;

    private String sessionId;

    private ChatContentType contentType;

    @NotBlank(message = "请提出问题")
    private String message;

//    private List<String> documentList;
//
//    private List<String> audioList;
//
//    private List<String> imageList;
}
