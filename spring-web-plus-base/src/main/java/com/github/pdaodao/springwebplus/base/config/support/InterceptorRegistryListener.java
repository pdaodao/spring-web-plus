package com.github.pdaodao.springwebplus.base.config.support;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

public interface InterceptorRegistryListener {

    void addInterceptors(InterceptorRegistry registry);
}
