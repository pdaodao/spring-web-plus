package com.github.pdaodao.springwebplus.base.config;

import com.github.pdaodao.springwebplus.base.service.TokenStore;
import com.github.pdaodao.springwebplus.base.service.impl.LocalTokenStore;
import com.github.pdaodao.springwebplus.base.service.impl.RedisTokenStore;
import com.github.pdaodao.springwebplus.base.service.impl.RocksTokenStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@AutoConfiguration
public class TokenStoreConfig {

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public TokenStore localTokenStore(SysConfigProperties configProperties) throws Exception{
//        return new LocalTokenStore(configProperties);
        return new RocksTokenStore();
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public TokenStore redisTokenStore(SysConfigProperties configProperties, @Qualifier("redisTemplate") RedisTemplate redisTemplate) {
        return new RedisTokenStore(configProperties, redisTemplate);
    }
}
