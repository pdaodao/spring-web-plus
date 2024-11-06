package com.github.pdaodao.springwebplus.base.config.fs.local;

import com.github.pdaodao.springwebplus.tool.fs.local.LocalConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fs.local")
public class LocalFsConfig extends LocalConfig {

}