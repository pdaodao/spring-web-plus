package com.github.pdaodao.springwebplus.base;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@AutoConfiguration
@Order(Integer.MIN_VALUE)
@ComponentScan(basePackages = "com.github.pdaodao.springwebplus")
public class SpringwebplusAutoConfiguration {
}