package com.github.pdaodao.springwebplus.base.config;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

@Data
@Configuration
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

    // 登录地址
    @Value("${auth.login:}")
    private String loginUrl;

    // 登录过期有效时长 分钟 7天
    @Value("${auth.expire:10080}")
    private Integer authExpire;

    // 登陆活跃时长 分钟
    @Value("${auth.active:1}")
    private Integer authActive;

    // token 名称
    @Value("${auth.token:Token}")
    private String authTokenName;

    // 是否自动更新表结构
    @Value("${dao.ddl.gen.enabled:true}")
    private Boolean ddlGenEnabled;

    @Value("${dao.ddl.gen.delete:false}")
    private Boolean ddlGenDeleteField;

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    /**
     * http 代理配置 a -> http://baidu.com/aa, b-> ...
     */
    @Value("${http.proxy:}")
    private String httpProxy;

    @Value("${cors.path:/*/api/v1/**}")
    private String corsPath;

    @Value("${cors.origins:}")
    private String corsOrigins;

    @Value("${cors.head:Origin}")
    private String corsHead;


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

    public Integer authExpireSeconds(){
        if(authExpire < 3){
            return authExpire;
        }
        return authExpire * 60;
    }

    public Integer authActiveSeconds(){
        if(authActive < 3){
            return authActive;
        }
        return authActive * 60;
    }

    public String getLoginUrl() {
        if(StrUtil.isNotBlank(loginUrl)){
            return loginUrl;
        }
        return SpringUtil.getContextPath()+"/login";
    }
}
