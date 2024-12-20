package com.github.pdaodao.springwebplus.base.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.github.pdaodao.springwebplus.base.config.support.WithSubQueryPageInnerInterceptor;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ConditionalOnClass(MybatisPlusAutoConfiguration.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore(MybatisPlusAutoConfiguration.class)
@MapperScan("com.github.pdaodao.springwebplus.**.mapper")
public class MybatisPlusConfig {
    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        final MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        final WithSubQueryPageInnerInterceptor page = new WithSubQueryPageInnerInterceptor();
        page.setOptimizeJoin(false);
        mybatisPlusInterceptor.addInnerInterceptor(page);
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    @Bean
    public VendorDatabaseIdProvider databaseIdProvider() {
        return new VendorDatabaseIdProvider();
    }

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public DbFieldFillHandler fieldFillHandler() {
        return new DbFieldFillHandler();
    }
}
