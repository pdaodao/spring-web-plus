package com.github.pdaodao.springwebplus.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * i18n 国际化配置
 * <p>
 * 文件: src/main/resources/messages*.properties
 * <p>
 * 默认语言通过 application.properties 配置: spring.mvc.locale=zh_CN
 */
@Configuration
public class I18nConfig implements WebMvcConfigurer {

    public static final String LANGUAGE_PARAM = "lang";

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(LANGUAGE_PARAM);
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/public/**", "/*.html", "/error");
    }
}
