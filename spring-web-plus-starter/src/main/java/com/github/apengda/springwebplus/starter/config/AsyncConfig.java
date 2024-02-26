package com.github.apengda.springwebplus.starter.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 设置核心线程数
        executor.setMaxPoolSize(30);  // 设置最大线程数
        executor.setQueueCapacity(100); // 设置队列容量
        executor.setThreadNamePrefix("MyAsync-"); // 设置线程名前缀
        executor.initialize();
        return executor;
    }
}