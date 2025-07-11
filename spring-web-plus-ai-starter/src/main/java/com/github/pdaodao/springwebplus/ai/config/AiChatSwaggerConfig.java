package com.github.pdaodao.springwebplus.ai.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class AiChatSwaggerConfig {

    @Bean
    public GroupedOpenApi chatApi() {
        final String[] packagedToMatch = {" com.github.pdaodao.springwebplus.ai"};
        return GroupedOpenApi.builder()
                .group("ai-chat")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("ai-chat")))
                .packagesToScan(packagedToMatch)
                .build();
    }
}
