package com.github.apengda.springbootplus.core.config;

import com.github.apengda.springbootplus.core.util.SpringUtil;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi sysApi() {
        final String[] packagedToMatch = {"com.github.apengda.springbootplus"};
        return GroupedOpenApi.builder()
                .group("sys")
                .pathsToMatch("/**")
                .addOpenApiCustomiser(openApi -> openApi.info(new Info().title("System API")))
                .packagesToScan(packagedToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi appApi() {
        final String[] packagedToMatch = {SpringUtil.getBootPackage()};
        return GroupedOpenApi.builder()
                .group(SpringUtil.getAppName())
                .pathsToMatch("/**")
                .addOpenApiCustomiser(openApi -> openApi.info(new Info().title(SpringUtil.getAppName() + " API")))
                .packagesToScan(packagedToMatch)
                .build();
    }
}
