package com.github.apengda.springbootplus.core;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@ComponentScan(basePackages = "com.github.apengda.springbootplus")
@EnableAsync
@EnableCaching
public class SpringbootplusAutoConfiguration {
}
