package com.github.apengda.springwebplus.starter.config;

import com.github.apengda.springwebplus.starter.service.TokenStore;
import com.github.apengda.springwebplus.starter.service.impl.LocalTokenStore;
import com.github.apengda.springwebplus.starter.service.impl.RedisTokenStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class TokenStoreConfig {

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public TokenStore localTokenStore(SysConfigProperties configProperties){
        return new LocalTokenStore(configProperties);
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public TokenStore redisTokenStore(SysConfigProperties configProperties, RedisTemplate redisTemplate){
        return new RedisTokenStore(configProperties, redisTemplate);
    }
}
