package com.github.pdaodao.springwebplus.ai.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "test")
@AllArgsConstructor
@RequestMapping("/ai/api/v1/test")
public class TestController {
    //private final ChatClient chatClient;

    public void test() {
//        chatClient.prompt().stream()
//        ChatClient.builder(null).build().prompt()
//                .tools()
//                .call().content();
    }

//    public static void main(String[] args) {
//        final ToolCallback[] fd = ToolCallbacks.from(new WeatherTool());
//        System.out.println("hello");
//    }
}
