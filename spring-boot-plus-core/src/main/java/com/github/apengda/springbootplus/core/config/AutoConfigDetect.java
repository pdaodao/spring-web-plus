package com.github.apengda.springbootplus.core.config;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AutoConfigDetect implements AutoConfigurationImportFilter, EnvironmentAware {
    private Environment env;

    private static boolean hasClass(String name) {
        try {
            ClassUtil.loadClass(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean[] match(final String[] autoConfigurationClasses, final AutoConfigurationMetadata autoConfigurationMetadata) {
        final boolean[] arr = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            final String clazz = autoConfigurationClasses[i];
            if (clazz == null) {
                arr[i] = false;
                continue;
            }
            arr[i] = true;

            if (clazz.contains("RabbitAutoConfiguration")) {
                final String host = env.getProperty("spring.rabbitmq.host");
                if (StrUtil.isBlank(host)) {
                    arr[i] = false;
                }
            } else if (clazz.contains("JCacheCacheConfiguration")) {
                arr[i] = false;
            } else if (clazz.contains("RedisAutoConfiguration")) {
                final String host = env.getProperty("spring.redis.host");
                if (StrUtil.isBlank(host) || !hasClass("org.springframework.data.redis.cache.RedisCacheManager")) {
                    arr[i] = false;
                }
            }
        }
        return arr;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}

