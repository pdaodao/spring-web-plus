package com.github.pdaodao.springwebplus.base.config;

import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@Order(Integer.MIN_VALUE)
@AutoConfiguration(before = org.springdoc.core.configuration.SpringDocConfiguration.class)
@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class ApiDocConfig implements InitializingBean {
//    @Bean
//    public ModelConverter modelConverter(){
//        return new ModelConverter() {
//            @Override
//            public Schema resolve(AnnotatedType annotatedType, ModelConverterContext modelConverterContext, Iterator<ModelConverter> iterator) {
//                if(annotatedType.getType() instanceof Class<?> && annotatedType.getType() == CurrentUserInfo.class){
//                    return new Schema();
//                }
//                return iterator.hasNext() ? iterator.next().resolve(annotatedType, modelConverterContext, iterator) : null;
//            }
//        };
//    }

    @Bean
    public GroupedOpenApi currentAppApi() {
        final String[] packagedToMatch = {SpringUtil.getBootPackage()};
        return GroupedOpenApi.builder()
                .group("1_" + SpringUtil.getAppName())
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title(SpringUtil.getAppName() + " API")))
                 .packagesToExclude("com.github.pdaodao.springwebplus")
                .packagesToScan(packagedToMatch)
                .build();
    }


    @Bean
    public GroupedOpenApi sysApi() {
        final String[] packagedToMatch = {"com.github.pdaodao.springwebplus"};
        return GroupedOpenApi.builder()
                .group("sys")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info().title("System API")))
                .packagesToScan(packagedToMatch)
                .build();
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title(SpringUtil.getAppName())
                        .description("接口文档")
                        .version("v1"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.setProperty("springdoc.default-flat-param-object", "true");
    }
}
