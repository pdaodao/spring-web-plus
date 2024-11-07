package com.github.pdaodao.springwebplus.base.config.support;

import cn.hutool.crypto.digest.DigestUtil;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.core.io.InputStreamSource;

import java.lang.reflect.Method;


public class CacheKeyGenerator implements KeyGenerator {

    public Object generate(Object target, Method method, Object... params) {
        final StringBuilder sb = new StringBuilder();
        sb.append(method.getName()).append("(");
        if (params != null) {
            for (final Object obj : params) {
                if (obj == null) {
                    sb.append("null").append(",");
                    continue;
                }
                if (obj instanceof ServletResponse) continue;
                if (obj instanceof ServletRequest) continue;
                if (obj instanceof InputStreamSource) continue;
                sb.append(JsonUtil.toJsonString(obj));
                sb.append(obj).append(",");
            }
        }
        return DigestUtil.sha256Hex(sb.toString());
    }
}