package com.github.pdaodao.springwebplus.ai.service;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class WeatherTool {

    @Tool(description = "根据城市名称查询天气")
    public String info(@ToolParam(description = "城市名称", required = false) final String city,
                       final ToolContext toolContext) {

        return "天气晴朗";
    }
}
