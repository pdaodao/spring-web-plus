package com.github.apengda.springwebplus.starter.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SysConfigProperties {
    // 前端文件根路径
    @Value("${static.path:${user.dir}/webapp}")
    private String staticPath;

    // redis 缓存有效时间 分钟
    @Value("${spring.cache.redis.time-to-live:30}")
    private Long redisTTL;

    @Value("${spring.cache.caffeine.spec:}")
    private String caffeineSpec;
}
