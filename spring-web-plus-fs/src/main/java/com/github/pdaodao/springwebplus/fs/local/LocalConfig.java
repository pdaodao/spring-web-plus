package com.github.pdaodao.springwebplus.fs.local;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fs.local")
public class LocalConfig {
    public String rootPath;
}