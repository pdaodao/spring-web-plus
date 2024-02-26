package com.github.apengda.springwebplus.starter.config;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Configuration
@Data
public class SysConfigProperties {
    // 前端文件根路径
    @Value("${static.path:${user.dir}/webapp}")
    private String staticPath;

    // redis 缓存有效时间 分钟
    @Value("${spring.cache.redis.time-to-live:30}")
    private Long redisTTL;

    @Value("${spring.cache.caffeine.spec:}")
    private String caffeineSpec;

    // 验签排除路径
    @Value("${auth.excludes:}")
    private String authExcludes;


    public boolean authExcludeMatch(final String path) {
        if (StrUtil.isEmpty(getAuthExcludes())) {
            return false;
        }
        for (final String excludePath : StrUtil.split(getAuthExcludes(), ",")) {
            final AntPathMatcher antPathMatcher = new AntPathMatcher();
            boolean match = antPathMatcher.match(excludePath, path);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
