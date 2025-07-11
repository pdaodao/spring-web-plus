package com.github.pdaodao.springwebplus.ai.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan("com.github.pdaodao.springwebplus.ai")
@MapperScan("com.github.pdaodao.springwebplus.ai.mapper")
public class AiChatAutoConfig {
}