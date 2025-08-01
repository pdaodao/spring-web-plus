package com.github.pdaodao.flow.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan("com.github.pdaodao.flow")
@MapperScan("com.github.pdaodao.flow.**.mapper")
public class FlowAutoConfig {
}
