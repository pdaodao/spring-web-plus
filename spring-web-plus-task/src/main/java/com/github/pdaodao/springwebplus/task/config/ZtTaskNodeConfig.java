package com.github.pdaodao.springwebplus.task.config;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.util.IpUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ZtTaskNodeConfig {
    @Value("${zt.node.id:1}")
    private String nodeId;

    @Value("${zt.node.executor:true}")
    private Boolean isExecutor;

    @Value("${zt.node.admin:true}")
    private Boolean isAdmin;

    @Value("${zt.node.ip:}")
    private String ip;

    public String getHost(){
        final String myIp = StrUtil.isBlank(ip) ? IpUtil.getLocalhostIp() : ip;
        return StrUtil.format("http://{}:{}{}", myIp, SpringUtil.getPort(), SpringUtil.getContextPath());
    }
}
