package com.github.pdaodao.springwebplus.base.config;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({CacheProperties.Caffeine.class, CaffeineCacheManager.class})
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "caffeine", matchIfMissing = true)
@ConditionalOnMissingBean(CacheManager.class)
@AllArgsConstructor
public class CaffeineCacheConfig {
    final SysConfigProperties config;

    @Bean
    public CacheManagerCustomizer<CaffeineCacheManager> myCaffeineCustomizer() {
        if (StrUtil.isNotBlank(config.getCaffeineSpec())) {
            return null;
        }
        return cacheManager -> cacheManager.setCacheSpecification("initialCapacity=500,maximumSize=50000,expireAfterWrite=1800s");
    }

}
