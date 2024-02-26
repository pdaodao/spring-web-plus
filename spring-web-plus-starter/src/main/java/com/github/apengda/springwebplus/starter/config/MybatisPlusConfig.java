package com.github.apengda.springwebplus.starter.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
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
public class MybatisPlusConfig {

//    /**
//     * 分页插件
//     */
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
//        PageInnerInterceptor page = new PageInnerInterceptor();
//        page.setDbType(DbType.MYSQL);
//        if (dbConfig.isDm()) {
//            page.setDbType(DbType.DM);
//        } else if (dbConfig.isKingbase()) {
//            page.setDbType(DbType.KINGBASE_ES);
//        }
//        addSystemDbTableNameRewrite(mybatisPlusInterceptor);
//        mybatisPlusInterceptor.addInnerInterceptor(page);
//        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
//        return mybatisPlusInterceptor;
//    }
//

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public DbFieldFillHandler fieldFillHandler() {
        return new DbFieldFillHandler();
    }
}
