package com.github.apengda.springbootplus.core.config;

import com.github.apengda.springbootplus.core.config.support.CacheKeyGenerator;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheKeyConfig extends CachingConfigurerSupport {

    @Override
    public KeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }
}
