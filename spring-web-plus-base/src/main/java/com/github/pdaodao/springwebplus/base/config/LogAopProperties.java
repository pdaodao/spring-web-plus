package com.github.pdaodao.springwebplus.base.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "log-aop")
public class LogAopProperties {
    /**
     * 是否启用
     */
    private boolean enable = true;

    /**
     * 是否打印日志
     */
    private boolean print = true;

    /**
     * 排除的路径
     */
    private String excludePaths;

    public List<String> excludePathList(){
        if(StrUtil.isBlank(excludePaths)){
            return ListUtil.empty();
        }
        return StrUtil.split(excludePaths, ",");
    }
}
