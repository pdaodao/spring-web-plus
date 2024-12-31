package com.github.pdaodao.springwebplus.base.config;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.github.pdaodao.springwebplus.base.config.support.WithSubQueryPageInnerInterceptor;
import com.github.pdaodao.springwebplus.base.frame.PgBoolToIntTypeHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
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

    @Bean(name = "plusConfigurationCustomizer")
    public ConfigurationCustomizer configurationCustomizer() {
        final ConfigurationCustomizer customizer = configuration -> {
            final TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            typeHandlerRegistry.register(Boolean.class, new PgBoolToIntTypeHandler());
        };
        return customizer;
    }

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public DbFieldFillHandler fieldFillHandler() {
        return new DbFieldFillHandler();
    }

    @Bean
    public MybatisPlusPropertiesCustomizer plusPropertiesCustomizer() {
        return new MybatisPlusPropertiesCustomizer() {
            @Override
            public void customize(MybatisPlusProperties properties) {
                final String[] old = properties.getMapperLocations();
                final String names = StringUtils.join(old, ",");
                if (names.contains("citycloud")) {
                    return;
                }
                final String[] locations = ArrayUtil.addAll(old, new String[]{
                        "classpath*:/cn/com/citycloud/**/*.xml"
                });
                properties.setMapperLocations(locations);
            }
        };
    }
}
