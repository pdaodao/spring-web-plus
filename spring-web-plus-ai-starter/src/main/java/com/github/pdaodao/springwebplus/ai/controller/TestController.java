package com.github.pdaodao.springwebplus.ai.controller;

import com.github.pdaodao.springwebplus.ai.service.WeatherTool;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "test")
@AllArgsConstructor
@RequestMapping("/ai/api/v1/test")
public class TestController {
    private final ChatClient chatClient;

    public void test(){
//        ChatClient.builder(null).build().prompt()
//                .tools()
//                .call().content();
    }

    public static void main(String[] args) {
        final ToolCallback[] fd = ToolCallbacks.from(new WeatherTool());
        System.out.println("hello");
    }
}
