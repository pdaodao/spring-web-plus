package com.github.pdaodao.springwebplus.base.config;

import com.github.pdaodao.springwebplus.base.service.TokenStore;
import com.github.pdaodao.springwebplus.base.service.impl.LocalTokenStore;
import com.github.pdaodao.springwebplus.base.support.DbTokenStore;
import com.github.pdaodao.springwebplus.base.support.dao.SysUserTokenDao;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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

    @Bean
    @ConditionalOnBean(DataSource.class)
    public TokenStore dbTokenStore(SysConfigProperties configProperties, SysUserTokenDao tokenDao) {
        return new DbTokenStore(tokenDao);
    }
}
