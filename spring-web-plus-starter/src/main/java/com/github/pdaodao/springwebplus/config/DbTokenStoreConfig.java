package com.github.pdaodao.springwebplus.config;

import com.github.pdaodao.springwebplus.base.config.SysConfigProperties;
import com.github.pdaodao.springwebplus.base.config.TokenStoreConfig;
import com.github.pdaodao.springwebplus.base.service.TokenStore;
import com.github.pdaodao.springwebplus.dao.SysUserTokenDao;
import com.github.pdaodao.springwebplus.service.DbTokenStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;

@AutoConfiguration(before = TokenStoreConfig.class)
public class DbTokenStoreConfig {
    @Bean
    @ConditionalOnBean(DataSource.class)
    public TokenStore dbTokenStore(SysConfigProperties configProperties, SysUserTokenDao tokenDao) {
        return new DbTokenStore(tokenDao);
    }
}
