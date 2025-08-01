package com.github.pdaodao.flow.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class FlowSwaggerConfig {

    @Bean
    public GroupedOpenApi flowApi() {
        final String[] packagedToMatch = {"com.github.pdaodao.flow"};
        return GroupedOpenApi.builder()
                .group("workflow")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("workflow")))
                .packagesToScan(packagedToMatch)
                .build();
    }
}
