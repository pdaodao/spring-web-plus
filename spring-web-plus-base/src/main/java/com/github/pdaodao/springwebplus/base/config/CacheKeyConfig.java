package com.github.pdaodao.springwebplus.base.config;

import com.github.pdaodao.springwebplus.base.config.support.CacheKeyGenerator;
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
