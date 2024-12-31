package com.github.pdaodao.springwebplus.base.auth;

import com.github.pdaodao.springwebplus.base.config.support.InterceptorRegistryListener;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Component
@AllArgsConstructor
public class LoginInterceptorListener implements InterceptorRegistryListener {
    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/api/**", "/*/api/**");
    }
}
