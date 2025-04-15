package com.github.pdaodao.springwebplus.base.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pdaodao.springwebplus.base.config.support.WithSubQueryPageInnerInterceptor;
import com.github.pdaodao.springwebplus.base.frame.JsonArrayNodeHandler;
import com.github.pdaodao.springwebplus.base.frame.JsonObjectNodeHandler;
import com.github.pdaodao.springwebplus.base.frame.PgBoolToIntTypeHandler;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
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

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public DbFieldFillHandler fieldFillHandler() {
        return new DbFieldFillHandler();
    }

    @Bean(name = "plusConfigurationCustomizer")
    public ConfigurationCustomizer configurationCustomizer() {
        final ConfigurationCustomizer customizer = configuration -> {
            final TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            typeHandlerRegistry.register(ArrayNode.class, new JsonArrayNodeHandler());
            typeHandlerRegistry.register(ObjectNode.class, new JsonObjectNodeHandler());
        };
        return customizer;
    }

    @Bean
    public MybatisPlusPropertiesCustomizer plusPropertiesCustomizer() {
        return new MybatisPlusPropertiesCustomizer() {
            @Override
            public void customize(MybatisPlusProperties properties) {
                properties.getGlobalConfig().getDbConfig().setLogicDeleteField("is_deleted");
                properties.getGlobalConfig().getDbConfig().setLogicDeleteValue("true");
                properties.getGlobalConfig().getDbConfig().setLogicNotDeleteValue("false");
            }
        };
    }
}
