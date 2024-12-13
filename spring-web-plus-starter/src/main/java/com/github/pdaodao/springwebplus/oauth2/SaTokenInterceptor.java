package com.github.pdaodao.springwebplus.oauth2;

import com.github.pdaodao.springwebplus.base.config.support.InterceptorRegistryListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Component
public class SaTokenInterceptor implements InterceptorRegistryListener {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new SaInterceptor()
//                .setAuth(t -> StpUtil.checkLogin())).addPathPatterns("/api/**", "/*/api/**");
    }
}
