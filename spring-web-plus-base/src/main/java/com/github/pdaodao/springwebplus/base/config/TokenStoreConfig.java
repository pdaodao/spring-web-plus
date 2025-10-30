package com.github.pdaodao.springwebplus.base.config;

import com.github.pdaodao.springwebplus.base.service.TokenStore;
import com.github.pdaodao.springwebplus.base.service.impl.LocalTokenStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;

@AutoConfiguration
public class TokenStoreConfig {

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public TokenStore localTokenStore(SysConfigProperties configProperties) throws Exception{
        return new LocalTokenStore(configProperties);
    }
}
