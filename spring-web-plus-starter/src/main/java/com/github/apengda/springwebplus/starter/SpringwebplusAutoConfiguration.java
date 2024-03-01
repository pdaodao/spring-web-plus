package com.github.apengda.springwebplus.starter;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@ComponentScan(basePackages = "com.github.apengda.springwebplus")
public class SpringwebplusAutoConfiguration {
}