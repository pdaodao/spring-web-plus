package com.github.pdaodao.springwebplus.base.config.support;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.pdaodao.springwebplus.base.config.FirewallConfig;
import com.github.pdaodao.springwebplus.base.util.IpUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 防火墙
 */
@Slf4j
public class FirewallHandler implements HandlerInterceptor {
    // QPS限制缓存 - 存储每个IP+API的访问计数
    private final Cache<String, AtomicInteger> qpsCache = Caffeine.newBuilder()
            .maximumSize(100000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();
    // 配置信息
    private FirewallConfig config = new FirewallConfig();

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        if(config == null || BooleanUtil.isFalse(config.getEnabled())){
            return true;
        }
        final String ipAddress= IpUtil.getRequestIp();
        final String apiPath= request.getRequestURI();
        final String method= request.getMethod();
        log.debug("防火墙拦截检查: IP={}, API={}, Method={}", ipAddress, apiPath, method);
        try {
            // 1. 检查 ip是否允许访问
            final boolean ipAllowed = isIpAllowed(ipAddress);
            Preconditions.checkArgument(ipAllowed, "当前ip不允许访问.");
            // 2. 检查 qps 限制
            final boolean qpsAllowed = qpsAllowed(ipAddress, apiPath);
            Preconditions.checkArgument(qpsAllowed, "QPS限制拦截.");
            return true;
        } catch (Exception e) {
            log.error("防火墙拦截器处理异常: IP={}, API={}", ipAddress, apiPath, e);
        }
        return true;
    }

    private boolean qpsAllowed(final String ipAddress, final String apiPath){
        if(config.getEnabled() == false || config.getQpsApi() <= 0){
            return true;
        }
        final String key = ipAddress + ":" + apiPath;
        AtomicInteger counter= qpsCache.getIfPresent(key);
        if (counter == null) {
            counter = new AtomicInteger(0);
            qpsCache.put(key, counter);
        }
        int currentCount = counter.incrementAndGet();
        return currentCount <= config.getQpsApi();
    }

    private boolean isIpAllowed(final String ip) {
        if(StrUtil.startWith(ip, "127.0")){
            return true;
        }
        return true;
    }
}
