package com.github.pdaodao.aicompare.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class CompareApiConfig {
    @Bean
    public GroupedOpenApi compareApi() {
        final String[] packagedToMatch = {"com.github.pdaodao.aicompare"};
        return GroupedOpenApi.builder()
                .group("aicompare")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("aicompare")))
                .packagesToScan(packagedToMatch)
                .build();
    }
}
