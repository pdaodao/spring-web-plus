package com.github.pdaodao.springwebplus.base.config.fs.oss;

import com.github.pdaodao.springwebplus.tool.fs.oss.OssConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * oss 连接信息配置
 */
@Configuration
@ConfigurationProperties(prefix = "fs.oss")
public class OssFsConfig extends OssConfig {

}
