package com.github.apengda.springwebplus.starter.config;

import com.github.apengda.springwebplus.starter.util.SpringUtil;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class ApiDocConfig implements InitializingBean {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title(SpringUtil.getAppName())
                        .description("接口文档")
                        .version("v1"));
    }

    @Bean
    public GroupedOpenApi appApi() {
        final String[] packagedToMatch = {SpringUtil.getBootPackage()};
        return GroupedOpenApi.builder()
                .group("1." + SpringUtil.getAppName())
                .pathsToMatch("/**")
                .addOpenApiCustomiser(openApi -> openApi.info(new Info().title(SpringUtil.getAppName() + " API")))
                .packagesToExclude("com.github.apengda.springwebplus")
                .packagesToScan(packagedToMatch)
                .build();
    }


    @Bean
    public GroupedOpenApi sysApi() {
        final String[] packagedToMatch = {"com.github.apengda.springwebplus"};
        return GroupedOpenApi.builder()
                .group("sys")
                .pathsToMatch("/**")
                .addOpenApiCustomiser(openApi -> openApi.info(new Info().title("System API")))
                .packagesToScan(packagedToMatch)
                .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.setProperty("springdoc.default-flat-param-object", "true");
    }
}
