package com.github.apengda.springbootplus.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "测试")
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "hello")
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
}
