package com.github.pdaodao.springwebplus.base;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@ComponentScan(basePackages = "com.github.pdaodao.springwebplus")
public class SpringwebplusAutoConfiguration {
}