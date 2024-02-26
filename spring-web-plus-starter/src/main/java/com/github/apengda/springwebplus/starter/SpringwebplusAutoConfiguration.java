package com.github.apengda.springwebplus.starter;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@ComponentScan(basePackages = "com.github.apengda.springwebplus")
@EnableAsync
@EnableCaching
public class SpringwebplusAutoConfiguration {
}
